package monocle

import monocle.thirdparty.generic._

object GenericExample extends App {

  case class Person(name : String, age : Int, location: (Int, Int))

  val person = Person("Bob", 27, (3,4))

  import monocle.syntax.lens._

  println( person |-> _1 get )        // bob
  println( person |-> _3 |-> _1 get ) // 3

  println( person |-> _1 set "John"  )     //  Person("John", 27, (3,4))
  println( person |-> _2 modify (_ + 1) )  //  Person("Bob" , 28, (3,4))

}
