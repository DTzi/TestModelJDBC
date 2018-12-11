package modbat
import Array._
import scala.collection.mutable.HashSet
import java.sql.{SQLException}


class dbSim{
  var myarray = Array.ofDim[Any](2,10)
  val A = List("String1", "String2", "String3",
    "String4")//Using a small list for debugging.
  var initialized = false//checks if table has already initialized.
  var trackPk = Vector[Int]()//tracks Primary Keys.
  var returnDups = false
  def createTable(){
    //First check if table already exists.
    if (initialized == true) {
      //throw org.postgresql.util.PSQLException: ERROR: relation "table" already exists
      println("Table already exists")
    }
    //If not, initialise it.
    else{
      initialized = true
      var init = 0
      for(i <- 0 to myarray.length-1){
        init += 1
        myarray(i)(0) = init
      }
      //println("table created!")
    }
  }

  //Table exists-test Function.
  def returntable() :Boolean ={
     return initialized
  }

  //Function to test number of Columns in the JDBC model.
  def addCols(a:Int){
    //Initialise the table but the first Column.
    for(i <- 0 to myarray.length-1){
      for(j <- 1 to a){
        myarray(i)(j) = 0
      }
    }
  }

  //Test number of Columns.
  def returnCols() :Int ={
    var countCols = 0
    for(j <- 0 to myarray(0).length-1){
      if(myarray(0)(j) != null){
        countCols += 1
      }
    }
    return countCols
  }

  def addPk(a:Int){
    if(trackPk.contains(a)){
      //println("table has already a PK")
    }
    else{
      trackPk = trackPk :+ a
      //println("PK added")
    }
  }

  //Test PK.
  def returnPK() :Boolean ={
    if(trackPk.isEmpty){
      return false
    }
    else{
      return true
    }
  }

  //Add row param for the first itteration.
  def addData(a:Int, data:Vector[String]){
    var datacounter = 0
    for(i <- 0 to myarray.length-1){
      for(j <- 1 to a){
       //println(data(datacounter))
       myarray(i)(j) = data(datacounter)
       datacounter += 1
      }
    }
  }

  //function to find pk-duplicates.
  def check_for_pkDuplicates(a:Int, b:Int) :Boolean = {
    val mySet = HashSet.empty[Any]
    for(i <- 0 to myarray.length-1){
      mySet += myarray(i)(a)
      if(mySet.contains(myarray(i)(a))){
        returnDups = true
        clearDupsArray(b)//call it to clear the Duplicates row.
      }
    }
    return returnDups
  }

  def clearDupsArray(a:Int){
      for(j <- 1 to a){
       //println(data(datacounter))
       myarray(1)(j) = 0
     }
  }

  def deleteCol(a:Int){
    for(i <- 0 to myarray.length-1){
      //Just delete everything in the given Column.
      myarray(i)(a) = 0
    }
  }

  def deleteTable(){
    //Remove primary key first.
    trackPk = trackPk.filterNot(_ == trackPk.last)
    //And delete everything in the table.
    for(i <- 0 to myarray.length-1){
      for(j <- 0 to myarray(0).length-1){
        myarray(i)(j) = 0
      }
    }
  }

  //For debugging.
  def printArray(){
    for(i <- 0 to myarray.length-1){
      for(j <- 0 to myarray(0).length-1){
        println(myarray(i)(j))
      }
      println()
    }
  }
}

object dbSim{
  def main(args: Array[String]){
    //Create an array of objects.
    var mylist:Array[dbSim]= new Array[dbSim](10)
    //Create the objects.
    for(i <- 0 to mylist.length-1){
      mylist(i) = new dbSim()
    }
    /*get params through JDBC MODEL.
    Operations following JDBC model's transitions.
    Create table.
    mylist(0).createTable()
    mylist(0).createTable()//fails with ERROR: relation "table" already exists
    add 2 empty columns.
    mylist(0).addCols(2)
    add pk to the table.
    mylist(0).addPk(1)
    add some data and check if there are any duplicates in the Primary Key Column.
    mylist(0).addData(2)
    delete the third Column.
    mylist(0).deleteCol(2)
    mylist(0).printArray()*/

  }
}
