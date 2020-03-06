package monocle.state

import monocle.{Getter, MonocleSuite}

class ReaderExample extends MonocleSuite {
  case class Person(name: String, age: Int)
  val _age = Getter[Person, Int](_.age)
  val p    = Person("John", 30)

  test("ask") {
    val getAge = for {
      i <- _age ask
    } yield i

    getAge.run(p) shouldEqual (30)
  }

  test("asks") {
    val getDoubleAge = for {
      i <- _age asks (_ * 2)
    } yield i

    getDoubleAge.run(p) shouldEqual (60)
  }
}
