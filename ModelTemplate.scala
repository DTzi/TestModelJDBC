package modbat
import java.sql.{Connection, DriverManager, SQLException}

import modbat.dsl._

class ModelTemplate extends Model{
     Class.forName("org.postgresql.Driver")
     var con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test_db","postgres", "admin")
     //Mutation Connection 
     //var con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test")
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
     val dbTable = Array.ofDim[Any](2,10)
     val tableparam = choose(1, 10)//Table Parameter
     var table = ""
     val pkparam = choose(1, colparam)//Need for Test Functions
     var activeTransaction = false//toggle between Transitions.
     //dbSim variables.
     var initialized = false//checks if table has already initialized.
     var trackPk = false//tracks Primary Keys.
     var returnDuplicates = false
     
     //Create an array of objects.
     for(i <- 0 to mylist.length-1){
      mylist(i) = new dbSim(initialized, trackPk, returnDuplicates, dbTable)
    }
    //Initialise Cloned Object.
     var copyInit = new dbSim(initialized, trackPk, returnDuplicates, dbTable)



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

     def drop_column(a:Int){
          //val pkcol = choose(1, colparam)
          //println(pkcol)
          var dropCols = con.createStatement ()
          dropCols.executeUpdate ("ALTER TABLE " + table + " DROP COLUMN " + "column" + a)
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

     //test if the table is populated.
     def data_exist() :Boolean ={
          var stmt = con.prepareStatement("SELECT * FROM " + table)
          var rs = stmt.executeQuery()
          if (!rs.next() ) {
               return false
          }
          else{
               return true
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

     //Transitions

     "Start" -> "addData" :={
          val modelInsert = add_data(randData)
          val dbSimInsert = mylist(tableparam).addData(colparam, randData, randDates, randTypes)
     }catches("SQLException" -> "addData")
     "addData" -> "Init" :={
          assert(table_exists(tableparam) == mylist(tableparam).returntable())
     }
     "Init" -> "createTables" :={
          val createtableModel = create_table(tableparam)
          val createtabledbSim = mylist(tableparam).createTable()
          //Deep Copy an empty Instance.
          copyInit = mylist(tableparam).clone
          assert(table_exists(tableparam) == mylist(tableparam).returntable())
     }
     "createTables" -> "addCols" :={
          add_columns(colparam)
          mylist(tableparam).addCols(colparam)
     }
     "addCols" -> "testCols" := {
          val testColsModel = number_of_cols()
          val testColsdbSim = mylist(tableparam).returnCols()
          assert(testColsModel == testColsdbSim)
     }
     "testCols" -> "addPK" :={
          val addpkModel = add_pks(pkparam)
          val addpkdbSim = mylist(tableparam).addPk(pkparam)
     }
     "addPK" -> "testPK" :={
          assert(pk_exists() == mylist(tableparam).returnPK())
     }
     "testPK" -> "checkRequire" :={
          activeTransaction = true//Enable Transactions Active.
          con.setAutoCommit(false)//Begin Transaction.
          //Deep Copy the current empty Instance.
          val copy = mylist(tableparam).clone
          //Add some Data.
          mylist(tableparam).addData(colparam, randData, randDates, randTypes)
          add_data(randData)
          //Don't close the Transaction yet.
     }catches("SQLException" -> "checkRequire")
     "checkRequire" -> "fakecommit" :={
          if(activeTransaction==false){
               con.commit()
          }
     }
     "fakecommit" -> "commitOrrollback" :={
          if(choose(0,2)==0){
               con.commit()
               assert(data_exist==mylist(tableparam).returnData(colparam)) 
          }
          else{
               con.rollback()
               mylist(tableparam) = copyInit
               assert(data_exist==mylist(tableparam).returnData(colparam)) 
          }
     }

     



}