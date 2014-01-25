
import lens.Macro._
import lens.syntax._


object SyntaxExample extends App {

  case class Person(_age: Int, _name: String)

  object Person {
    val age  = mkLens[Person, Int]("_age")
    val name = mkLens[Person, String]("_name")
  }

  import Person._

  val person = Person(23, "roger")
  val newPerson = person >- age modify(_ + 2)
  println(newPerson)

  import lens.std.Map._
  val map = Map(1 -> "one", 2 -> "two", 3 -> "three")

  println(map >- at(1) get)
  println(map >- at(1) set Some("zero"))
  println(map >- at(1) set None)
  println(map >- at(6) get)
}


