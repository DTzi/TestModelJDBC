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

    var table = ""//tablename
    val random_columns = scala.util.Random/* Random number of Columns, replace with modbat's built_in function  def chooseName(i: Int) = {
    parameterName(choose(0, i))} */

    val random_tables = scala.util.Random
    var a = random_columns.nextInt(9)//random number of Tables.
    System.out.println(a)
    var counter = 0
    var col = 0
    var colArray = Vector[Int]()//Use the vector to remember the random_numbers to use them later.

    def create_table{

      for(x <- 1 to a){

        table = "table"+x
        var createTable = con.createStatement ()
             createTable.executeUpdate ("CREATE TABLE " + table
                 + "(id INTEGER )")
            }
          }


   def add_columns{

     for(x <- 1 to a){

      col = random_columns.nextInt(9)
      colArray = colArray :+ col
      //System.out.println(col)

      table = "table"+x

         var s1 = "ALTER TABLE "
         var s2 = table
         var st = s1.concat(s2)
         var s3 = " add "
         var st2 = st.concat(s3)
         var stz = con.createStatement()

         for(b <- 1 to col) {


         var columnName = "Column" +b
         var alter_table = st2 + columnName + " varchar(30)"
         stz.executeUpdate(alter_table)


          }
        }
      }

      def add_pks{

        counter = 0//set an upper limit for tables, check it through the vector.
        for(x<- colArray){

          //Have to use if-else statement, due to exception (Bound must be positive).
          if (x != 0) {
            pkcol = random_columns.nextInt(x)//assign random_number according to the number of columns.
            }
            else{
              pkcol = 0
            }

            counter+=1
            table = "table"+counter//tableName
            var str_pk = "Alter table "
            var str_pk2 = table
            var str_pkc = str_pk.concat(str_pk2)// Query -> Alter table tableName
            //System.out.println(str_pkc)
            var str_pk3 = " Add primary key"
            var str_pkcc = str_pkc.concat(str_pk3)// Query -> Alter table tableName Add primary key
            var str_pkcol = " (Column"+pkcol+")"//Get columnName
            var complete_add_pks = str_pkcc.concat(str_pkcol)// Query -> Alter table tableName Add primary key ColumnName

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
  }

    def add_data{
      counter = 0//set an upper limit for tables, check it through the vector.

        for(x <- colArray) {

          var sample="( ?";//named prepared_statement for data.
          counter+=1

            for(b<-1 to x){

              sample+=", ?";
              }
              sample+=" )";//get the amount of data to be inserted.
              //System.out.println(sample)
              //System.out.println("stop")

              var str1 = "INSERT INTO "
              table = "table"+counter
              var str2 = table
              var strr = str1.concat(str2)//get the String Query <INSERT INTO TABLE VALUES>
              //System.out.println(strr) DEBUG:
              var query = strr +
                       " VALUES " + sample

                      //  System.out.println(query) DEBUG:
                      //  var st = con.prepareStatement("INSERT INTO table16 VALUES (?)");
                      var st = con.prepareStatement(query)
                      // var p=con.prepareStatement(st);
                      st.setInt(1,331)//Get random integer/string.
                      var d = 0
                      for(d<-1 to x){

                          st.setString(1 + d, "Hello World.")//get random integer/string.
                          System.out.println(2 + d)

                        }

                        var rs=st.executeUpdate()


                        //create named_prepared_statement
                }

              }

    def drop_table{
        //drop tables for multiple tests.
        for(x <- 1 to a){

          table = "table"+x
          var dropTable = con.createStatement ()
          dropTable.executeUpdate ("DROP TABLE " + table)
                        }
                    }

              //TODO: Add transitions for PKS, FKS, ChangeDataType, Joins and other Queries.
    "Init" -> "tables" :=create_table
    "tables" -> "columns" :=add_columns
    "columns" -> "primary keys" :=add_pks
    "primary keys" -> "data" :=add_data
    "data" -> "delete tables" :=drop_table

}
