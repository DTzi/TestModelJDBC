import Array._

//Start simple and simulate the dabatase.
//==========================================================================================
//Init tables if they do not exist, with first column, exactly like JDBC model (createTable).
//Populate tables with any datatype.
//TODO add the remaining queries.

object dbSim{
  var myarray = Array.ofDim[Any](5,4,2)//set of 5 arrays with 4 rows, 2 cols.
  var trackTables = Vector[Int]()//tracks tables that already created.
  var countTables = 0//counts the number of tables created.
  //My DB tables for arguments just for this program. Choose table according to RNG of table Name.
  var table1 = 0
  var table2 = 1
  var table3 = 2
  var table4 = 3
  var table5 = 4
  var init = 0//Initialise the first col.

  def printArray(st: Array[Array[Array[Any]]], a:Int ){
    for(i <- a to a){
      for(j <- 0 to 3){
        for(k <- 0 to 1){
        println(myarray(i)(j)(k))
            }
            println()
          }
        }
        //println("number of tables created :" + countTables)
      }

  def initArray(st: Array[Array[Array[Any]]], a:Int){
    //First check if table already exists.
    if (trackTables.contains(a)) {
      println("Table already exists")
    }
    //If not, initialise it.
    else{
      countTables += 1
      init = 0
        for(i <- a to a){
         for(j <- 0 to 3){
           init += 1
            for(k <- 0 to 0){
             myarray(i)(j)(k) = init
                  }
               }
             }
             println("table created!")
           }
    }

  def addData(st: Array[Array[Array[Any]]], a:Int){
    //countTables += 1
    for(i <- a to a){
     for(j <- 0 to 3){
       for(k <- 1 to 1){
         myarray(i)(j)(k) = "get random element from RNG. "
              }
           }
         }
       }

  def main(args: Array[String]){
    //first initialise the chosen table, populate the first column like JDBC Model create_table.
    initArray(myarray, table1)//table1 -> a = random_columns.nextInt
    trackTables = trackTables :+ table1
    initArray(myarray, table1)
    println(countTables)//for DEBUG:
    //println(trackTables)
    initArray(myarray, table2)
    trackTables = trackTables :+ table2
    println(countTables)
    //populate the array, similar to JDBC Model (add data).
    addData(myarray, table1)
    addData(myarray, table2)
    //Print for debugging.
    println("table1:")
    printArray(myarray, table1)
    println("table2:")
    printArray(myarray, table2)
    //3rd table is empty.
    println("table3:")
    printArray(myarray, table3)
    }
}
