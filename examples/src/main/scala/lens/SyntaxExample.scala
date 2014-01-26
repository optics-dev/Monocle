package lens

import lens.syntax._
import lens.syntax.std._
import scala.language.postfixOps


object SyntaxExample extends App {
  import Person._
  import Location._
  import lens.std.Map._

  val p = Person(23, "roger", Address("London", "EC1...", Location(2, 10)))
  val newPerson = p >- age modify(_ + 2)
  println(newPerson)

  val map = Map("Paris" -> Location(12, 34), "London" -> Location(10, 50))

  println(map >- at("Paris") get)
  println(map >- at("Pairs") set Some(Location(14, 18)))
  println(map >- at("Pairs") set None)
  println(map >- at("Roma") get)

  println(map >- at("Paris") >- option[Location] >- latitude set 89)

  println(List(1,2,3) >-- list[Int] set 3)          // ==> List(3,3,3)
  println(List(1,2,3) >-- list[Int] modify (_ + 1)) // ==> List(2,3,4)
}