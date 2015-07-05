package monocle.state

import monocle.MonocleSuite
import monocle.macros.GenLens

class StateExample extends MonocleSuite {

  case class Person(name: String, age: Int)
  val _age = GenLens[Person](_.age)
  val p = Person("John", 30)

  test("mod"){
    val increment = for {
      i <- _age mod (_ + 1)
    } yield i

    increment.run(p) shouldEqual ((Person("John", 31), 31))
  }

  test("modo"){
    val increment = for {
      i <- _age modo (_ + 1)
    } yield i

    increment.run(p) shouldEqual ((Person("John", 31), 30))
  }

  test("assign"){
    val set20 = for {
      i <- _age assign 20
    } yield i

    set20.run(p) shouldEqual ((Person("John", 20), 20))
  }

  test("assigno"){
    val set20 = for {
      i <- _age assigno 20
    } yield i

    set20.run(p) shouldEqual ((Person("John", 20), 30))
  }

}
