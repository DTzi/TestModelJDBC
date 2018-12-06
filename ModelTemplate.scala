package modbat
import java.sql.{Connection, DriverManager, SQLException}

import modbat.dsl._
//modbat.dsl.Model.assert

class ModelTemplate extends Model{
     Class.forName("org.postgresql.Driver")
     var con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test_db","postgres", "admin")
     var databaseMetaData = con.getMetaData()//get the information needed for the test functions.
     var stat = con.createStatement()
     val A = List("String1", "String2", "String3",
          "String4", "String5", "String6", "String7", "String8")
     //create a list with random data to pass into add_data, in model and dbSim.
     var col = 0
     var colArray = Vector[Int]()//remember the random numbers to use them later.
     var randData = Vector[String]()//list of Random Strings.
     var pkcol = 0
     val colparam = choose(1, 9)//Number of Cols.
     var mylist:Array[dbSim]= new Array[dbSim](10)
     for(i <- 0 to mylist.length-1){
      mylist(i) = new dbSim()
    }
     val tableparam = choose(1, 10)//Table Parameter
     var table = ""


     def create_table(a:Int){
          table = "table" + a
          colArray = colArray :+ a//get table number.
          var createTable = con.createStatement()
          createTable.executeUpdate ("CREATE TABLE " + table
               + "(column0 INTEGER )")
     }

     def add_columns(a:Int){
          var stz = con.createStatement()
          for(b <- 1 to a) {
               var alter_table = ("ALTER TABLE " + table + " add Column" + b + " varchar(30)" )
               stz.executeUpdate(alter_table)
          }
     }

     def add_pks(a:Int){
          var complete_add_pks = "Alter table " + table + " Add primary key (Column" + a + ")"
          stat.executeUpdate(complete_add_pks)
     }

     def add_data(data:Vector[String]) :Boolean ={
          //if there are duplicates in PK col, throw org.postgresql.util.PSQLException: ERROR: multiple primary keys for table "table" are not allowed.
          var sample="( ?"//named prepared_statement for data.
          for(b<-1 to colparam){
               sample+=", ?"
          }
          sample+=" )"//get the amount of data to be inserted.
          var query = "INSERT INTO " + table + " VALUES " + sample
          //println(query)
          var st = con.prepareStatement(query)
          var count = 0
          var datacounter = 0//Counts up to colparam * 2, which represents the columns * 2 rows.
          var returnResult = 0//Store the result of executeUpdate.
          for(d <- 1 to 2){//Number of Rows.
               st.setInt(1,d)//First Column.
               for(f <- 1 to colparam){
                    //Checking Data..
                    //println(randData(datacounter))
                    st.setString(1 + f, randData(datacounter))//Random string for each column.
                    datacounter += 1
               }
               st.addBatch()//This makes the trick for efficient "insert into Multiple Rows".
               count += 1
               if (count % 10 == 0){
                    st.executeBatch()
               }
               var rs = st.executeUpdate()//Returns: either (1) the row count for SQL Data Manipulation Language (DML) statements
                                          //or (2) 0 for SQL statements that return nothing
               returnResult = rs
          }
          //check if executeUpdate is successful.
          if(returnResult > 0){
            return true
          }
          else{
            return false
          }
     }

     def change_dataType{
          for(x <- colArray) {
               if (x != 0) {
                    val pkcol = choose(0, x)//assign random_number according to the number of columns.
               }
               else{
                    pkcol = 0
               }
               var string1 = "ALTER table "+table + " ALTER COLUMN " + " column" + pkcol
               var string2 = " TYPE INT"
               var final_string = string1.concat(string2)
               var string3 = " USING " + "column" + pkcol + " ::integer"
               var final_ = final_string.concat(string3)
               //println(final_)
               stat.executeUpdate(final_)
          }
     }

     def joins{
          //Inner Join
          var Inner_join = con.createStatement()
          //Requires(n_table >= 2)
          var random_joins = colArray(choose(0, colArray.size))
          //System.out.println("first random table " + random_joins)
          colArray = colArray.filterNot(_ == random_joins)
          //var new_random_joins = colArray.filter(_ != random_joins)
          //colArray(Random.nextInt(colArray.size))
          var second_joins = colArray(choose(0, colArray.size))
          var rs = Inner_join.executeQuery("SELECT table" + random_joins + ".column1, table" + second_joins + ".column1 AS COL1 FROM table" + random_joins + " INNER JOIN table" + second_joins +" ON table" +random_joins + ".column0= table" + second_joins + ".column0")
          System.out.println("Col, Data:")
          while (rs.next()) {
               var column1 = rs.getString("Column1")
               var column2 = rs.getString("COL1")
               System.out.println("    " + column1 + ", " + column2)
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
          val testColsModel = number_of_cols
          val testColsdbSim = mylist(tableparam).returnCols()
          assert(testColsModel == testColsdbSim)
     }
     "test columns" -> "add primary key" :={
          val pkparam = choose(1, colparam)
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
     "test PK" -> "add data" :={
       try{
          val modelInsert = add_data(randData)
          val dbSimInsert = mylist(tableparam).addData(colparam, randData)
          assert(modelInsert == dbSimInsert)
        }catch{
          case e: SQLException => e.printStackTrace//Ignore SQLException.
        }
     }/*label "SQLException" catches("SQLException" -> "Check ExceptionIsValid") -> Go to the next Transition and check result.
     "check ExceptionIsValid" -> "Next"
     "add data" -> "test data" := data_exist*/

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
