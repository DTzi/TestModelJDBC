package modbat
import Array._
import scala.collection.mutable.HashSet//For Duplicates.
import java.sql.{SQLException}



 class dbSim(var init:Boolean, var prkey:Boolean, var dupes:Boolean, val myarray:Array[Array[Any]]) extends Cloneable{
  
  override def clone = { 
    val clonedArray = myarray.clone 
    //Deep Copy the DB table. 
    for (i <- 0 to myarray.length - 1){ 
      clonedArray(i) = myarray(i).clone 
    } 
    new dbSim(init, prkey, dupes, clonedArray) 
  }  
  
  def createTable(){
    //First check if table already exists.
    if (init == true) {
      //throw org.postgresql.util.PSQLException: ERROR: relation "table" already exists
    }
    //If not, initialise it.
    else{
      init = true
      /*var initcount = 0
      for(i <- 0 to myarray.length-1){
        initcount += 1
        myarray(i)(0) = initcount
      }*/
    }
  }

  //Table exists-test Function.
  def returntable() :Boolean ={
     return init
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
    var countCols = 1
    for(j <- 0 to myarray(0).length-1){
      if(myarray(0)(j) != null){
        countCols += 1
      }
    }
    return countCols
  }

  def addPk(a:Int){
    if(prkey == true){
      //println("table has already a PK")
    }
    else{
      prkey = true
      //println("PK added")
    }
  }

  //Test PK.
  def returnPK() :Boolean ={
    if(prkey == false){
      return false
    }
    else{
      return true
    }
  }

  //Test Data.
  def returnData(a:Int) :Boolean ={
    var empty = false
    for(j <- 1 to a){
      if(myarray(0)(j) != null){
        empty = true
      }
      else{
        empty = false
      }
    }
    return empty
  }

  //Add row param for the first itteration.
  def addData(a:Int, data:Vector[String], dates:Vector[String], types:Vector[Int]){
    var datacounter = 0
    for(i <- 0 to myarray.length-1){
      for(j <- 1 to a){
        if(types(datacounter) == 1){
          myarray(i)(j) = dates(datacounter)
        }
        else{
          myarray(i)(j) = data(datacounter)
        }
       //println(data(datacounter))
       datacounter += 1
      }
    }
  }

  //function to find Primary Key-duplicates.
  def check_for_pkDuplicates(a:Int, b:Int) :Boolean = {
    val mySet = HashSet.empty[Any]
    for(i <- 0 to myarray.length-1){
      mySet += myarray(i)(a)
      if(mySet.contains(myarray(i)(a))){
        dupes = true
        clearDuplicates(b)//call it to clear the row with Duplicates.
      }
    }
    return dupes
  }

  def clearDuplicates(a:Int){
      for(j <- 1 to a){
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
    prkey = false
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
    /*Create an array of objects.
    var mylist:Array[dbSim]= new Array[dbSim](10)
    Create the objects.
    for(i <- 0 to mylist.length-1){
      mylist(i) = new dbSim()
      //mylist(i) = new dbSim(Array.ofDim[Any](2,10))

    }
    get params through JDBC MODEL.
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
