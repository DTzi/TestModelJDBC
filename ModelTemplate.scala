package modbat
import scala.util.Random;
import java.util.Arrays;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import modbat.dsl._

class ModelTemplate extends Model {


  def init_connect {



      Class.forName("org.postgresql.Driver");

      var con = DriverManager.getConnection("jdbc:postgresql://localhost:/dbname","postgres", "user");
      var databaseMetaData = con.getMetaData();//Get Column_Names and attributes.
      var stat = con.createStatement();
      //var counter = 0;



      var table = "";
      var a = 0; //use for number of tables.
      //get random number of tables. a should be random.


      //insert random number of columns PRIORITY.
        for( a <- 0 to 10){
          //var attrs = A(Random.nextInt(A.size))//pick a random attribute.

          table = "table"+a;//name of the table, table0 table1 ...
          //create table.
          var createTable = con.createStatement ();
            createTable.executeUpdate ("CREATE TABLE " + table
                + "(id INTEGER )");

                   //var columnNumber = 5; Number of columns, randomly choose number of columns.
          val random_cols = scala.util.Random
          var col = random_cols.nextInt(7) //get number of columns randomly.
                  //DEBUG::
          var b = 0;
                   //Insert by altering table the number of columns.
           for( b <- 1 to col){
              var s1 = "ALTER TABLE "
              var s2 = table;
              var st = s1.concat(s2);//String Manipulation, need to change.TODO
              var s3 = " add ";
              var st2 = st.concat(s3);//String Manipulation, need to change.TODO
              var stz = con.createStatement();
              var columnName = "Column" +b;//Name of columns, column1 column2 ...
              var alter_table = st2 + columnName + " varchar(30)";//Get data_type randomized with altering.
              System.out.println(alter_table);//DEBUG::
              //Do not use executeQuery.
              stz.executeUpdate(alter_table);
              //var sql = "ALTER TABLE MyDB.myTable ADD " + columnName + " INTEGER(30)";
            }

            var sample="( ?";
            //for every added column, insert into random data.
            for(b<-1 to col)
            {
              sample+=", ?";
            }
            sample+=" )";
            //System.out.println(sample);//DEBUG::

                //Fill with data. Randomly choose data from a list or dictionary.
                //create the Query string to insert random data.
                var str1 = "INSERT INTO "
                var str2 = table;
                var strr = str1.concat(str2);//get the String Query <INSERT INTO TABLE VALUES>
                System.out.println(strr);//// DEBUG:
                //var samples = "(?)"
                 var query = strr +
                                   " VALUES " + sample;

                                    System.out.println(query);//// DEBUG:
                                  //  var st = con.prepareStatement("INSERT INTO table16 VALUES (?)");
                                  var st = con.prepareStatement(query);
                // var p=con.prepareStatement(st);
                 //Increment here, for values 1,2..
                 st.setInt(1,random_Int);
                 var d = 0;
                 for(d<-1 to col)
                 {

                   st.setString(1 + d, "random_String");
                   System.out.println(2 + d);

                 }
                 var rs=st.executeUpdate();

                 //START DEBUGGING FOR getPrimaryKeys
                 //Work with Metadata.

          //get pks and then randomly pick a new one.
          var pk_change = con.createStatement();
          var pk = 0;
          var alter_pk = "Alter table table2 Add primary key (id)";
          stat.executeUpdate(alter_pk);

          var PK = databaseMetaData.getPrimaryKeys(null,null, table);
          System.out.println("------------PRIMARY KEYS-------------");
          while(PK.next())
          {
            System.out.println(PK.getString("COLUMN_NAME") + "===" + PK.getString("PK_NAME"));
          }
          //Alter datatype.
          //statement.executeUpdate("ALTER TABLE table_name ALTER COLUMN column_name datatype");




    }



  }

  "one" -> "end" :=init_connect
}
