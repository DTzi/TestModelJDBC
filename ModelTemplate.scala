package modbat
import java.sql.{Connection, DriverManager, SQLException}

import modbat.dsl._

class ModelTemplate extends Model{
     Class.forName("org.postgresql.Driver")
     var dbConnection = null
     //Clean Installation Connection.
     //var con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test_db","postgres", "admin")
     //Mut Connection.
     var con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test")
     var databaseMetaData = con.getMetaData()//get the information needed for the test functions.
     var stat = con.createStatement()//Used by Primary Key.
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
     val myarraytodc = Array.ofDim[Any](2,10)
     val tableparam = choose(1, 10)//Table Parameter
     var table = ""
     val pkparam = choose(1, colparam)//Need for Test Functions
     var activeTransaction = false//toggle between Transitions.

     //dbSim variables.
     var initialized = false//checks if table has been created.
     var trackPk = false//tracks Primary Keys.
     var returnDuplicates = false//check for Duplicate Data.
     var copyInit = new dbSim(initialized, trackPk, returnDuplicates, myarraytodc)//Cloned object.
     
     //Create an array of objects.
     for(i <- 0 to mylist.length-1){
      mylist(i) = new dbSim(initialized, trackPk, returnDuplicates, myarraytodc)
    }

     //Create a table.
     def create_table(a:Int){
          table = "table" + a
          colArray = colArray :+ a//get table number.
          var createTable = con.createStatement()
          createTable.executeUpdate ("CREATE TABLE " + table
               + "(column0 INTEGER )")
     }

     //Add Cols.
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

     //Add a Primary Key.
     def add_pks(a:Int){
          var complete_add_pks = "Alter table " + table + " Add primary key (Column" + a + ")"
          stat.executeUpdate(complete_add_pks)
     }

     //Add Data according to Cols data type.
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
                    //assert(mylist(tableparam).isValidDate(randDates(datacounter)))//Validate Date Input.
                    st.setDate(1 + f, java.sql.Date.valueOf(randDates(datacounter)))//Random Dates.
                    st.addBatch()//This makes the trick for efficient "insert into Multiple Rows".
                  }
                  else{
                    //assert(mylist(tableparam).isValidDate(randDates(datacounter)))//Validate Date Input.
                    //st.setDate(1 + f, java.sql.Date.valueOf(randDates(datacounter)))//Random Dates.
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

     //Delete a Col.
     def drop_column{
          //pkcol = myRng.nextInt(col)
          val pkcol = choose(1, colparam)
          //System.out.println("DELETE COLUMN " + pkcol)
          var dropCols = con.createStatement ()
          dropCols.executeUpdate ("ALTER TABLE " + table + " DROP COLUMN " + "column" + pkcol)
     }

     //Validate Date Input.
     def validate() :Boolean ={
          var valid = false
          var counter = 0//itterate over random types to get all dates.
          for(d <- 1 to 2){//Number of Rows.
               for(f <- 1 to colparam){
                    if(randTypes(counter) == 1){
                         //println("validate fun")
                         //println(randDates(counter))
                         if((mylist(tableparam).isValidDate(randDates(counter)))){
                         valid = true
                         }
                         else{
                         valid = false
                         }
                    }    
               counter += 1
               }
          }
          valid
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
     def data_exists() :Boolean ={
          var empty = false
          var checkdata = con.prepareStatement("SELECT * FROM " + table)
          var rs = checkdata.executeQuery()
          if (!rs.next()) {
               empty = false
          }
          else{
               empty = true
          }
          rs.close()
          empty
     }

     //test if the table is populated.
     def data_exist() {
          var empty = true
          var resultsCounter = 0
          var ps = con.prepareStatement("SELECT * FROM " + table)
          val rs = ps.executeQuery()
          //val ResultSet = statement.executeQuery(sql)
          while(rs.next()){
               resultsCounter += 1
          }
          println(resultsCounter)
     }

     //test number of rows
     def row_count() {
          var ps = con.prepareStatement("SELECT count(*) FROM " + table)
          var rs = ps.executeQuery()
          rs.next()
          var count = rs.getInt(1)
          println(count)
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
     //@Before
     def create_data{
       for(i <- 1 to colparam * 2){
         var random_string = A(choose(0, A.size))
         randData = randData :+ random_string
       }
     }

     def clear_data{
          randData = scala.collection.immutable.Vector.empty
          randDates = scala.collection.immutable.Vector.empty
          randTypes = scala.collection.immutable.Vector.empty
     }

     def print_data{
          //debug
          println(randData)
     }

     //Create a list of random dates.
     //@Before
     def create_dates{
       for(i <- 1 to colparam * 2){
         var random_date = B(choose(0, B.size))
         randDates = randDates :+ random_date
       }
     }

     //Create a list of random datatypes before the Tests. Pass it to create Cols in Oracle and Model.
     //@Before
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

     //Model Transition Protorype
     /*a" -> "b" := {
          val params = chooseParameter(s)
          val systemResult = database.action(params)
          val modelResult = dbSim.action(params)
          checkResult(systemResult, modelResult)
    }*/

     //DB Transitions-------------------------------------------------------------------------------------------------------------------------------------------

     //clear all for dups and reroll data.
     "Init data" -> "Init" :={
          choose_datatypes
          create_data
          create_dates
          /*println("dates")
          println(randDates)
          println("types")
          println(randTypes)*/
     }

     "Init" -> "Cols" :={
          //Create a table.
          val createtableModel = create_table(tableparam)
          val createtabledbSim = mylist(tableparam).createTable()
          //Deep Copy an empty Instance in case of Rolling back.
          copyInit = mylist(tableparam).clone

          //Check if table already exists.
          assert(table_exists(tableparam) == mylist(tableparam).returntable())
     }

     "Cols" -> "testCols" :={
          //Add Cols.
          add_columns(colparam)
          mylist(tableparam).addCols(colparam)
     }

     "testCols" -> "PK" := {
          //Test Cols.
          val testColsModel = number_of_cols()
          val testColsdbSim = mylist(tableparam).returnCols()
          assert(testColsModel == testColsdbSim)
     }

     "PK" -> "testPK" :={
          //Add PK.
          val addpkModel = add_pks(pkparam)
          val addpkdbSim = mylist(tableparam).addPk(pkparam)
     }

     "testPK" -> "addData" :={
          //Test PK.
          assert(pk_exists() == mylist(tableparam).returnPK())
     }

     "addData" -> "checkCommit" :={
          //Begin Transaction ->>>>>>
          con.setAutoCommit(false)//Begin Transaction.

          //Add some Data.
          mylist(tableparam).addData(colparam, randData, randDates, randTypes)
          add_data(randData)

          //check for non-dupes
          val dbSimDuplicates = mylist(tableparam).check_for_pkDuplicates(pkparam, colparam)
          assert(!dbSimDuplicates) 

          //check for valid input
          validate

     }catches("SQLException" -> "checkExc", "IllegalArgumentException" -> "checkExc")

     "checkExc" -> "checkCommit" :={
          var reasonFound = false//flag for Exc.

          //check for invalid input
          if(!validate){
               //println("!isvalid")
               reasonFound = true
          }
          //con.close() close the connection to avoid transcation error.
          
          //check for duplicates.
          val dbSimDuplicates = mylist(tableparam).check_for_pkDuplicates(pkparam, colparam)
          // no negation (dup. found) 
          if(dbSimDuplicates){
               //println("dups")
               reasonFound = true
          }
               
     assert(reasonFound)
     //If a Query fails, we have to rollback to avoid Transaction Aborted Exc.
     con.rollback()
     mylist(tableparam) = copyInit//Clone

     }

     "checkCommit" -> "reopenCon" :={  
          if(choose(0,2)==0){
               con.commit()
               con.close()
          }
          else{
               con.rollback()
               mylist(tableparam) = copyInit//Clone
               con.close()
          }
     }

     "reopenCon" -> "Er1" :={
          //Reconnect to DB
          //con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test_db","postgres", "admin")
          con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test")
          //We check if both tables contain data or not
          assert(data_exists==mylist(tableparam).returnData(colparam))
    }

     "Er1" -> "Er2" :={
          //Creating the same table, should throw an Exception.
          create_table(tableparam)
          mylist(tableparam).createTable()
     }catches("SQLException" -> "Err1")
     "Err1" -> "Er2" :={
          //....
     }


}
