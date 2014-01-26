package lens

import lens.syntax._
import scala.language.postfixOps


object SyntaxExample extends App {
  import Person._
  import lens.std.Map._

  val p = Person(23, "roger", Address("London", "EC1...", Location(2, 10)))
  val newPerson = p >- age modify(_ + 2)
  println(newPerson)

  val map = Map(1 -> "one", 2 -> "two", 3 -> "three")

  println(map >- at(1) get)
  println(map >- at(1) set Some("zero"))
  println(map >- at(1) set None)
  println(map >- at(6) get)
}


