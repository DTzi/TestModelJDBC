import Array._
import scala.util.Random
import scala.collection.mutable.HashSet

//Start simple and simulate the dabatase.
//==========================================================================================
//Create tables if they do not exist, with first column, exactly like JDBC model (createTable).
//Populate tables with any datatype
//Detele Tables
//Check PK uniqueness - Insert PK.
//Detele Col
//TODO ->
//Add columns
//Add Joins
//Change Data Type

object dbSim{
  val A = List("String1", "String2", "String3",
    "String4")
  var myarray = Array.ofDim[Any](5,4,2)//set of 5 arrays with 4 rows, 2 cols.
  var trackTables = Vector[Int]()//tracks tables that already created.
  var Cols = Vector[Int]()//tracks columns.
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

  def addCol(a:Int){
    //Add number of Columns.
  }

  def deleteCol(st: Array[Array[Array[Any]]], a:Int, b:Int){
    for(k <- a to a){ 
      for(i <- b to b){
        for(j <- 0 to myarray(1).length-1){
          //Just delete everything in the given Column.
          myarray(k)(j)(i) = 0
        }    
      }
    }
  }

  def deleteTable(st: Array[Array[Array[Any]]], a:Int){
    if (trackTables.contains(a)) {
      trackPk = trackPk.filterNot(_ == trackPk.last)
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
    //Just check if we can insert into the table created 
    //countTables += 1
    for(k <- a to a){ 
      for(i <- 1 to 1){
        for(j <- 0 to myarray(1).length-1){
        //var rng = "get random element from RNG. "
        var random_string = A(Random.nextInt(A.size))
        //println(random_string)
        myarray(k)(j)(i) = random_string
       }
      }
    }
  }

  //helper function for PK uniqueness.    
  def addDataUtil(st: Array[Array[Array[Any]]], a:Int, b:Int){
    //Check for Duplicate Data only in the PK Col.
    for(k <- a to a){ 
      //for(i <- 0 to myarray(0)(1).length-1){
      for(i <- b to b){
        val mySet = HashSet.empty[Any]
        for(j <- 0 to myarray(1).length-1){
          //Check for duplicates in the set of Column Data.
          println("check for Pk")
          println(myarray(k)(j)(i))
          if (mySet.contains(myarray(k)(j)(i))) println("duplicate")
            mySet += myarray(k)(j)(i)
        }    
      }
    }
  }

  def main(args: Array[String]){

    //create table, and print it.
    initTable(myarray, table2)//table1 -> a = random_columns.nextInt
    trackTables = trackTables :+ table2
    //addCol()
    trackPk = trackPk :+ table2
    //println("Printing Initialised Table")
    printArray(myarray, table2)
    //println("Added data")
    addData(myarray, table2) 
    printArray(myarray, table2)

    deleteCol(myarray, table2, 1)
    printArray(myarray, table2)

    deleteTable(myarray, table2)
    //println("Printing Empty table and Removed PK")
    printArray(myarray, table2)

    initTable(myarray, table2)
    trackTables = trackTables :+ table2
    trackPk = trackPk :+ table2
    addData(myarray, table2) 
    println("")
    addDataUtil(myarray, table2, trackPk.last)
    printArray(myarray, table2)

  }
}
