  import Array._

  //Start simple and simulate the dabatase.
  //==========================================================================================
  //Init tables if they do not exist, with first column, exactly like JDBC model (createTable).
  //Populate tables with any datatype.
  //Add Primary Key.
  //Add Delete table.
  //Add columns, delete columns.
  //More queries.

  object dbSim{
    var myarray = Array.ofDim[Any](5,4,2)//set of 5 arrays with 4 rows, 2 cols.
    var trackTables = Vector[Int]()//tracks tables that already created.
    var trackCols = Vector[Int]()//tracks columns.
    var trackPk = Vector[Int]()//tracks pk.
    var countTables = 0//counts the number of tables created.
    //My DB tables for arguments just for this program. Choose table according to RNG of table Name.
    var table1 = 0
    var table2 = 1
    var table3 = 2
    var table4 = 3
    var table5 = 4
    var init = 0//Initialise the first col.

    def printArray(st: Array[Array[Array[Any]]], a:Int){
        for(j <- 0 to 3){
          for(k <- 0 to 1){
          println(myarray(a)(j)(k))
              }
              println()
            }
          //println("number of tables created :" + countTables)
        }

    def addCol(st: Array[Array[Array[Any]]], a:Int, b:Int){

    }

    def deleteCol(st: Array[Array[Array[Any]]], a:Int, b:Int){

    }

    def deleteTable(st: Array[Array[Array[Any]]], a:Int){
      if (trackTables.contains(a)) {
        //table exists, delete it.
        println("table exists, deleting")
        trackTables = trackTables.filterNot(_ == a)
        for(j <- 0 to 3){
          for(k <- 0 to 1){
            myarray(a)(j)(k) = 0
                 }
              }
            }
          else{
            println("table doesnt't exist")
          }
      }

    def checkPK(b:Int){
      //Simulate Insert-Pk.
      //Check if PK applies. 
          println("primary key added at column:" + b)
          trackPk :+ b
    }

    def initTable(st: Array[Array[Array[Any]]], a:Int){
      //First check if table already exists.
      if (trackTables.contains(a)) {
        println("Table already exists")
      }
      //If not, initialise it.
      else{
        countTables += 1
        init = 0
           for(j <- 0 to 3){
             init += 1
              for(k <- 0 to 0){
               myarray(a)(j)(k) = init
                    }
                 }

               println("table created!")
             }
      }

    def addData(st: Array[Array[Array[Any]]], a:Int){
      //countTables += 1
      //add pk check.
       for(j <- 0 to 3){
         for(k <- 1 to 1){
           myarray(a)(j)(k) = "get random element from RNG. "
                }
             }
         }

    def main(args: Array[String]){
      //first initialise the chosen table, populate the first column like JDBC Model create_table.
      
      //create table, and print it.
      initTable(myarray, table2)//table1 -> a = random_columns.nextInt
      trackTables = trackTables :+ table2
      println("printing initialised table ->")
      printArray(myarray, table2)

      //delete table, and print it.
      deleteTable(myarray, table2)
      println("table should be empty")
      printArray(myarray, table2)
      //double-check for detele error.
      deleteTable(myarray, table2)
      //create table again.
      initTable(myarray, table2)
      println("table should have only first col initialised")
      printArray(myarray, table2)
      trackTables = trackTables :+ table2

      initTable(myarray, table2)//throws error table exists.
      //trackPk(1)

      /*addData(myarray, table2)//table exists, add data.
      deleteTable(myarray, table2)//delete table with data.
      println(trackTables)

      println("table2: should be empty")//table should be empty.
      printArray(myarray, table2)

      
      }
  }
