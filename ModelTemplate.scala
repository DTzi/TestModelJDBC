package modbat
import java.sql.{Connection, DriverManager, SQLException}

import modbat.dsl._

class ModelTemplate extends Model{
     Class.forName("org.postgresql.Driver")
     var con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test_db","postgres", "admin")
     var databaseMetaData = con.getMetaData()//get the information needed for the test functions.
     var stat = con.createStatement()
     val A = List("String1", "String2", "String3",
          "String4", "String5", "String6", "String7", "String8")//create a list with random data to pass into add_data, in model and dbSim.
     val B = List("2011-09-28","2001-09-28","2001-11-28", "2001-10-01",
          "2001-09-18", "2018-01-01", "2019-02-03", "2004-05-06" )//List of Dates.
     var col = 0
     var colArray = Vector[Int]()//remember the random numbers to use them later.
     var randData = Vector[String]()//list of Random Strings.
     var randDates = Vector[String]()//list of Random Dates.
     var randTypes = Vector[Int]()//list of Random Data Types.
     var pkcol = 0
     val colparam = choose(1, 9)//Number of Cols.
     var mylist:Array[dbSim]= new Array[dbSim](10)
     for(i <- 0 to mylist.length-1){
      mylist(i) = new dbSim()
    }
     val tableparam = choose(1, 10)//Table Parameter
     var table = ""
     val pkparam = choose(1, colparam)//Need for Test Functions



     def create_table(a:Int){
          table = "table" + a
          colArray = colArray :+ a//get table number.
          var createTable = con.createStatement()
          createTable.executeUpdate ("CREATE TABLE " + table
               + "(column0 INTEGER )")
     }

     def add_columns(a:Int){
          var stz = con.createStatement()
          var data_types_counter = 0
          for(b <- 1 to a) {
            //Assign data types according to random datatypes List.
               if(randTypes(data_types_counter) == 0){
                 var alter_table = ("ALTER TABLE " + table + " add Column" + b + " varchar(30) " )
                 stz.executeUpdate(alter_table)
               }
               else{
                 var alter_table = ("ALTER TABLE " + table + " add Column" + b + " date " )
                 stz.executeUpdate(alter_table)
               }
               data_types_counter += 1
          }
     }

     def add_pks(a:Int){
          var complete_add_pks = "Alter table " + table + " Add primary key (Column" + a + ")"
          stat.executeUpdate(complete_add_pks)
     }

     def add_data(data:Vector[String]){
           var sample="( ?"//named prepared_statement for data.
           for(b<-1 to colparam){
                sample+=", ?"
           }
           sample+=" )"//get the amount of data to be inserted.
           var query = "INSERT INTO " + table + " VALUES " + sample
           var st = con.prepareStatement(query)
           var count = 0
           var datacounter = 0//Counts up to colparam * 2, which represents the columns * 2 rows.
           for(d <- 1 to 2){//Number of Rows.
                st.setInt(1,d)//First Column.
                for(f <- 1 to colparam){
                  if(randTypes(datacounter) == 1){
                    st.setDate(1 + f, java.sql.Date.valueOf(randDates(datacounter)))//Random Dates.
                    st.addBatch()//This makes the trick for efficient "insert into Multiple Rows".
                  }
                  else{
                    st.setString(1 + f, randData(datacounter))//Random string for each column.
                    st.addBatch()
                  }
                  datacounter += 1
                }
                count += 1
                if (count % 10 == 0){
                     st.executeBatch()
                }
                var rs = st.executeUpdate()
              }
      }

     def drop_column{
          //pkcol = myRng.nextInt(col)
          val pkcol = choose(1, colparam)
          //System.out.println("DELETE COLUMN " + pkcol)
          var dropCols = con.createStatement ()
          dropCols.executeUpdate ("ALTER TABLE " + table + " DROP COLUMN " + "column" + pkcol)
     }

     //checkResult test functions
     //test if table exists.
     def table_exists(a:Int) :Boolean ={
          table = "table" + a//Need to re-initialise? avoid always true error.
          var rs = databaseMetaData.getTables(null, null, table, null)
          if (rs.next()) {
                return true
               //throw org.postgresql.util.PSQLException: ERROR: relation "table" already exists.
          }
          else {
               return false
          }
     }

     //test number of columns.
     def number_of_cols() :Int ={
          var ps = con.prepareStatement("SELECT * FROM " + table)
          var rs = ps.executeQuery()
          var getcols = rs.getMetaData()
          return getcols.getColumnCount()
     }

     //test Primary key.
     def pk_exists() :Boolean ={
           //Verify pks are correctly assigned to the table.
          var PK = databaseMetaData.getPrimaryKeys(null, null, table)
          if (PK.next()) {
                return true
          }
          else {
               return false
          }
     }

     //Create a list of random data and pass it to the add_Data functions(Model and Oracle).
     @Before
     def create_data{
       for(i <- 1 to colparam * 2){
         var random_string = A(choose(0, A.size))
         randData = randData :+ random_string
       }
     }

     //Create a list of random dates.
     @Before
     def create_dates{
       for(i <- 1 to colparam * 2){
         var random_date = B(choose(0, B.size))
         randDates = randDates :+ random_date
       }
     }

     //Create a list of random datatypes before the Tests. Pass it to create Cols in Oracle and Model.
     @Before
     def choose_datatypes{
       for(i <- 1 to colparam){
         var random_data_type = choose(0, 2)
         randTypes = randTypes :+ random_data_type
       }
       randTypes = randTypes ++ randTypes//Every col has to be the same twice for addbatch() to work.
     }

     //Drop everything before the transitions.
     @Before
     def drop_Tables{
       for(i <- 0 to 9){
         table = "table" + i
         var dropTable = con.createStatement()
         dropTable.executeUpdate ("DROP TABLE IF EXISTS " + table)
       }
     }

     //Model Transition
     /*a" -> "b" := {
          val params = chooseParameter(s)
          val systemResult = database.action(params)
          val modelResult = dbSim.action(params)
          checkResult(systemResult, modelResult)
    }*/

     "Init" -> "create table" :={
          //put transactions inside the transitions.
          val createtableModel = create_table(tableparam)
          val createtabledbSim = mylist(tableparam).createTable()
          //val throwserror = mylist(tableparam).createTable()//calling createTable() again throws error.
          assert(table_exists(tableparam) == mylist(tableparam).returntable())
     }
     "create table" -> "add some columns" :={
          add_columns(colparam)
          mylist(tableparam).addCols(colparam)
     }
     "add some columns" -> "test columns" := {
          val testColsModel = number_of_cols()
          val testColsdbSim = mylist(tableparam).returnCols()
          assert(testColsModel == testColsdbSim)
     }
     "test columns" -> "add primary key" :={
          val addpkModel = add_pks(pkparam)
          val addpkdbSim = mylist(tableparam).addPk(pkparam)
          //assert PKS.
     }
     "add primary key" -> "test PK" :={
          assert(pk_exists() == mylist(tableparam).returnPK())
     }

     //Example Exception
     //Postgres exception org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint "table9_pkey"
     //Detail: Key (column2)=(String2) already exists. at add_data:

     //"test PK" -> "add data" :={
      /*try{
         val modelInsert = add_data(randData)
         val dbSimInsert = mylist(tableparam).addData(colparam, randData)
         assert(modelInsert == dbSimInsert)
       }catch{
         case e: SQLException => e.printStackTrace//Ignore SQLException.
       }
    }*/

    "test PK" -> "add data" :={
          val dbSimInsert = mylist(tableparam).addData(colparam, randData, randDates, randTypes)
          //val dbSimPrint = mylist(tableparam).printArray()
          val modelInsert = add_data(randData)
          //assert(modelInsert == dbSimInsert)
          //Always true, unless there is an Exception, maybe don't need to test it at all.
    }catches("SQLException" -> "check Duplicates")

    "check Duplicates" -> "Next Transition" :={
          //Test the exception here.
          //mylist(tableparam).printArray() - print before removing dups.
          val dbSimDuplicates = mylist(tableparam).check_for_pkDuplicates(pkparam, colparam)
          assert(dbSimDuplicates)
          //mylist(tableparam).printArray() - print after removing dups.
    }

     //Old Transitions
     //"add cols" -> "add primary key" := add_pks
     //"add primary key" -> "test primary key" := test_pk//test Primary key
     //"test primary key" -> "check if table exists" :=test_table//detect if table exists and table Name.
     //"check if table exists" -> "get number of columns" := test_cols//get the number of columns.
     //"create table" -> "add primary key" := add_pks
     //"add primary key" -> "test exception pk" := add_pks
     //"create table" -> "add columns" :=add_columns
     //"add columns" -> "add primary key" :=add_pks
     //"add primary key" -> "add data" :=add_data
     //"add data" -> "override data" :=add_data
     //"add data" -> "create another table" :=create_table
     //"create another table" -> "add columns to the new table" :=add_columns
     //"add columns to the new table" -> "remove one random column" :=drop_column
     //"add columns to the new table" -> "add data to the new table" :=add_data
     //"add data to the new table" -> "add joins" :=joins
    }
