package monocle.state

import monocle.{Getter, MonocleSuite}

import scala.annotation.nowarn

@nowarn
class ReaderExample extends MonocleSuite {
  case class Person(name: String, age: Int)
  val _age = Getter[Person, Int](_.age)
  val p    = Person("John", 30)

  test("ask") {
    val getAge = for {
      i <- _age ask
    } yield i

    assertEquals(getAge.run(p), 30)
  }

  test("asks") {
    val getDoubleAge = for {
      i <- _age asks (_ * 2)
    } yield i

    assertEquals(getDoubleAge.run(p), 60)
  }
}
