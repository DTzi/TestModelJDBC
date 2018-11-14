package modbat
import scala.util.Random
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

import modbat.dsl._

class ModelTemplate extends Model{
     Class.forName("org.postgresql.Driver")
     var con = DriverManager.getConnection("jdbc:postgresql://localhost:/dbname","postgres", "admin")
     var databaseMetaData = con.getMetaData()//get the information needed for the test functions.
     var stat = con.createStatement()
     val A = List("String1", "String2", "String3",
          "String4", "String5", "String6", "String7", "String8")
     var table = ""//tablename
     var a = 0
     var col = 0
     var colArray = Vector[Int]()//remember the random numbers to use them later.
     var pkcol = 0
     val colparam = choose(1, 9)//Number of Cols. Need to make it local.
     var mylist:Array[dbSim]= new Array[dbSim](10)
     for(i <- 0 to mylist.length-1){
      mylist(i) = new dbSim()
    }
     val tableparam = choose(1,5)//Make it local?



     def create_table (a:Int){
          table = "table" + a
          println(table)
          colArray = colArray :+ a//get table number to use in Joins.
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
          //pkcol = myRng.nextInt(col)
          var complete_add_pks = "Alter table " + table + " Add primary key (Column" + a + ")"
          stat.executeUpdate(complete_add_pks)
     }

     def add_data{
          //if there are duplicates in PK col, throw org.postgresql.util.PSQLException: ERROR: multiple primary keys for table "table" are not allowed.
          var sample="( ?"//named prepared_statement for data.
          for(b<-1 to col){
               sample+=", ?"
          }
          sample+=" )"//get the amount of data to be inserted.
          var query = "INSERT INTO " + table + " VALUES " + sample
          //println(query)
          var st = con.prepareStatement(query)
          var count = 0
          for(d <- 1 to 2){//Number of Rows.
               st.setInt(1,d)//First Column.
               for(f<-1 to col){
                    var random_string = A(Random.nextInt(A.size))//Get Random Strings from the List.
                    println(random_string)
                    st.setString(1 + f, random_string)//Random string for each column.
               }
               st.addBatch()//This makes the trick for efficient "insert into Multiple Rows".
               count += 1
               if (count % 10 == 0){
                    st.executeBatch()
               }
               var rs = st.executeUpdate()
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

     def drop_table{
          //drop tables for multiple tests.
          var dropTable = con.createStatement()
          dropTable.executeUpdate ("DROP TABLE " + table)
     }

     def joins{
          //Inner Join
          var Inner_join = con.createStatement()
          //Requires(n_table >= 2)
          var random_joins = colArray(Random.nextInt(colArray.size))
          //System.out.println("first random table " + random_joins)
          colArray = colArray.filterNot(_ == random_joins)
          //var new_random_joins = colArray.filter(_ != random_joins)
          var second_joins = colArray(Random.nextInt(colArray.size))
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

     //test if table exists.
     def test_table{
          var rs = databaseMetaData.getTables(null, null, table, null)
          if (rs.next()) {
               System.out.println("Table exists")
               //throw org.postgresql.util.PSQLException: ERROR: relation "table" already exists.
          }   
          else {
               System.out.println("Table does not exist")
          }
     }

     //test number of columns.
     def test_cols{
          var ps = con.prepareStatement("SELECT * FROM " + table)
          var rs = ps.executeQuery()
          var getcols = rs.getMetaData()
          //var colsnumber = getcols.getColumnCount()
          println("number of columns :" + getcols.getColumnCount())  
     }

     //test Primary key.
     def test_pk{
           //Verify pks are correctly assigned to the table. 
          var PK = databaseMetaData.getPrimaryKeys(null, null, table)
          while(PK.next())
          {
          System.out.println(PK.getString("COLUMN_NAME") + "===" + PK.getString("PK_NAME"))
        }
     }


     "Init" -> "create table" :={ 
          val createtableModel = create_table(tableparam)
          val createtabledbSim = mylist(tableparam).createTable()
          val throwserror = mylist(tableparam).createTable()//calling createTable() again throws error.
     }
     "create table" -> "add some columns" :={ 
          val addcolsModel = add_columns(colparam)
          val addcolsdbSim = mylist(tableparam).addCols(colparam)
     }

     "add some columns" -> "add primary key" :={
          val pkparam = choose(1, colparam)
          val addpkModel = add_pks(pkparam)
          val addpkdbSim = mylist(tableparam).addPk(pkparam)

     }

     //"create table" -> "add cols" := add_columns
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
