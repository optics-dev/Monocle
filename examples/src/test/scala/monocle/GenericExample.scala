package monocle

import monocle.syntax.lens._
import monocle.thirdparty.generic._
import org.specs2.scalaz.Spec

class GenericExample extends Spec {

  case class Person(name : String, age : Int, location: (Int, Int))

  val person = Person("Bob", 27, (3,4))

  "_1 creates a Lens from a Generic (typically case class) and its first element" in {
    (person |-> _1 get)        shouldEqual "Bob"
    (person |-> _1 set "John") shouldEqual Person("John", 27, (3,4))
  }

  "_i creates a Lens from a Generic (typically case class) and its ith element" in {
    (person |-> _3 |-> _1 get)     shouldEqual 3
    (person |-> _2 modify (_ + 1)) shouldEqual Person("Bob" , 28, (3,4))
  }

}
