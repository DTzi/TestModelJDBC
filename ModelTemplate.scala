package modbat
import scala.util.Random;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import modbat.dsl._

class ModelTemplate extends Model {
    Class.forName("org.postgresql.Driver");

    var con = DriverManager.getConnection("jdbc:postgresql://localhost:-/dbname","postgres", "User")
    var databaseMetaData = con.getMetaData();//Get Column_Names. Use it for pks,fks,datatype,joins.
    var stat = con.createStatement();
    //Use the list to get random Strings. TODO Have lists for specific columns NAMES, CITIES, SALARY ETC.
    val A = List("String1", "String2", "String3",
    "String4", "String5", "String6", "String7", "String8")

    var table = ""//tablename
    val random_columns = scala.util.Random/* Random number Generator, replace with modbat's built_in function  def chooseName(i: Int) = {
    parameterName(choose(0, i))} */
    val random_tables = scala.util.Random// New RNG.
    //var a = random_columns.nextInt(6) + 5, use for certain limits
    //var counter = 0 -> For multiple tables.
    var col = 0
    //var colArray = Vector[Int]()//Use the vector to remember the random_numbers to use them later.
    var pkcol = 0
    var a = 0
    var colArray = Vector[Int]()//vector, remember the random_numbers to use them later in Joins.


    def create_table{

        a = random_columns.nextInt(15) + 1
        table = "table" + a
        colArray = colArray :+ a//get table number to use in Joins.
        var createTable = con.createStatement ()
             createTable.executeUpdate ("CREATE TABLE " + table
                 + "(column0 INTEGER )")

          }


   def add_columns{


      col = random_columns.nextInt(10) + 1
      //System.out.println(col)

         var stz = con.createStatement()

         for(b <- 1 to col) {

           var columnName = "Column" +b
           var alter_table = ("ALTER TABLE " + table + " add Column" + b + " varchar(30)" )
           stz.executeUpdate(alter_table)

          }

      }

      def add_pks{

        //counter = 0 set an upper limit for tables, check it through the vector.

            pkcol = random_columns.nextInt(col)

            //table = "table"+counter//tableName
            var complete_add_pks = "Alter table " + table + " Add primary key (Column" + pkcol + ")"
            //System.out.println("PRIMARY KEY ADDED")

            stat.executeUpdate(complete_add_pks)

            /*Get Pks and verify they are correctly assigned to the table. Use an extra Method for that
            to use later for joins and ChangeDataType
            var PK = databaseMetaData.getPrimaryKeys(null,null, table)
            System.out.println("------------PRIMARY KEYS-------------")
            while(PK.next())
            {
              System.out.println(PK.getString("COLUMN_NAME") + "===" + PK.getString("PK_NAME"))
            }*/


  }

    def add_data{

          var sample="( ?";//named prepared_statement for data.

            for(b<-1 to col){

              sample+=", ?";
              }
              sample+=" )";//get the amount of data to be inserted.
              //System.out.println(sample)
              //System.out.println("stop")
              var query = "INSERT INTO " + table + " VALUES " + sample

                //System.out.println(query) DEBUG:
                //  var st = con.prepareStatement("INSERT INTO table16 VALUES (?)");
                var st = con.prepareStatement(query)
                // var p=con.prepareStatement(st);
                var d = 0
                var count = 0

                  for(d<-1 to 5){//Number of Rows. Could be random.

                      st.setInt(1,d)//First Column.

                        for(f<-1 to x){//And here Insert for the rest of the Columns.
                          var random_string = A(Random.nextInt(A.size))//Get Random Strings from the List.
                          //var random_string = s"name_${Random.alphanumeric take 10 mkString}" -> Get True random Strings.
                          //var random_string = "string" + r.nextInt(100) Get Strings with enumeration.
                          st.setString(1 + f, random_string)//Random string for each column.
                        }
                          st.addBatch()//This makes the trick for efficient "insert into Multiple Rows".
                          count += 1
                          if (count % 10 == 0){
                          //System.out.println("hi there!")
                          st.executeBatch()
                              }

                        var rs=st.executeUpdate()

                      }



               }

    def drop_table{
        //drop tables for multiple tests.

          //table = "table"+x
          var dropTable = con.createStatement ()
          dropTable.executeUpdate ("DROP TABLE " + table)

                    }

    def change_dataType{
        //Need to clean up.
        //counter = 0 set an upper limit for tables, check it through the vector.

            //counter+=1
            //table = "table"+counter
            var string1 = "ALTER table "+table
            //System.out.println(string1)
            var string2 = " ALTER COLUMN " + "column" + pkcol
            var string3 = string1.concat(string2)
            //System.out.println(string3)
            var string4 = " TYPE INT"
            var final_string = string3.concat(string4)
            //System.out.println(final_string)
            var string5 = " USING " + "column" + pkcol + " ::integer"
            var final_ = final_string.concat(string5)
            //System.out.println(final_)
            stat.executeUpdate(final_)

                    }

    def drop_column{

            pkcol = random_columns.nextInt(col)
            //System.out.println("DELETE COLUMN " + pkcol)

              var dropCols = con.createStatement ()
              dropCols.executeUpdate ("ALTER TABLE " + table + " DROP COLUMN " + "column" + pkcol)


                  }


    def joins{
        //Inner Join
        var Inner_join = con.createStatement()
        //Requires(n_table >= 2)
        //val rnd = new Random
        //var z =  rnd.nextInt(colArray.size)
        var random_joins = colArray(Random.nextInt(colArray.size))
        //System.out.println("first random table " + random_joins)
        colArray = colArray.filterNot(_ == random_joins)
        //var new_random_joins = colArray.filter(_ != random_joins)
        var second_joins = colArray(Random.nextInt(colArray.size))
        //System.out.println("second random table " + second_joins)

        //System.out.println("SELECT table" + random_joins + ".column1, table" + random_joins + " AS COL1 FROM table" + random_joins + " INNER JOIN table" + second_joins +" ON table" +random_joins + ".column0= table" + second_joins + ".column0")
        var rs = Inner_join.executeQuery("SELECT table" + random_joins + ".column1, table" + second_joins + ".column1 AS COL1 FROM table" + random_joins + " INNER JOIN table" + second_joins +" ON table" +random_joins + ".column0= table" + second_joins + ".column0")

                System.out.println("Col, Data:");
                    while (rs.next()) {
                        var column1 = rs.getString("Column1");
                        var column2 = rs.getString("COL1");

                            System.out.println("    " + column1 + ", " + column2);

                          }

            }

      /*"Init" -> "tables" :=create_table
      "tables" -> "columns" :=add_columns
      "columns" -> "primary keys" :=add_pks
      "primary keys" -> "data" :=add_data
      "data" -> "change data type" :=change_dataType throws ("org.postgresql.util.PSQLException")//The exception ALWAYS occurs
      "change data type" -> "delete tables" :=drop_table//Need to delete for more than one tests.*/

      //New Flexible transitions, one Operation per call. Need to Rename transitions.
      "Init" -> "create table" :=create_table
      "create table" -> "add columns" :=add_columns
      "add columns" -> "add primary key" :=add_pks
      "add primary key" -> "add data" :=add_data
      "add data" -> "create new table" :=create_table
      "create new table" -> "add columns to the new table" :=add_columns
      //"add columns to the new table" -> "remove one random column" :=drop_column
      "add columns to the new table" -> "add data to the new table" :=add_data
      "add data to the new table" -> "add joins" :=joins
      //"call data again" -> "call tables twice" :=create_table
      //"call tables twice" -> "call columns twice" := add_columns
      //"call columns twice" -> "call primary keys twice" :=add_pks
      //"call primary keys twice" -> "call data again twice" :=add_data





}
