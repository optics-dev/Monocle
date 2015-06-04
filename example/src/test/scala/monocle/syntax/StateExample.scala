package monocle.syntax

import monocle.MonocleSuite
import monocle.macros.GenLens

class StateExample extends MonocleSuite {

  case class Person(name: String, age: Int)
  val _age = GenLens[Person](_.age)
  val p = Person("John", 30)

  test("updateState"){
    val increment = for {
      i <- _age ~= (_ + 1)
    } yield i

    increment.run(p) shouldEqual ((Person("John", 31), 30))
  }

  test("assign"){
    val increment = for {
      _ <- _age := 20
    } yield ()

    increment.run(p) shouldEqual ((Person("John", 20), ()))
  }

}
