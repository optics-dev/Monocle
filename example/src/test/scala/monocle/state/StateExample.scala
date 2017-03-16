package monocle.state

import monocle.{MonocleSuite, Optional, Getter}
import monocle.macros.GenLens

class StateExample extends MonocleSuite {

  case class Person(name: String, age: Int)
  val _age = GenLens[Person](_.age)
  val p = Person("John", 30)

  test("extract"){
    val getAge = for {
      i <- _age extract
    } yield i

    getAge.run(p).value shouldEqual ((Person("John", 30), 30))
  }

  test("extracts"){
    val getDoubleAge = for {
      i <- _age extracts (_ * 2)
    } yield i

    getDoubleAge.run(p).value shouldEqual ((Person("John", 30), 60))
  }

  val _oldAge = Optional[Person, Int](p => if (p.age > 50) Some(p.age) else None){ a => _.copy(age = a) }
  val _coolGuy = Optional[Person, String](p => if (p.name.startsWith("C")) Some(p.name) else None){ n => _.copy(name = n) }

  test("extract for Optional (predicate is false)"){
    val youngPerson = Person("John", 30)
    val update = _oldAge extract

    update.run(youngPerson).value shouldEqual ((Person("John", 30), None))
  }

  test("extract for Optional (predicate is true)"){
    val oldPerson = Person("John", 100)
    val update = _oldAge extract

    update.run(oldPerson).value shouldEqual ((Person("John", 100), Some(100)))
  }

  test("extracts for Optional (predicate is false)"){
    val youngPerson = Person("John", 30)
    val update = _oldAge extracts (_ * 2)

    update.run(youngPerson).value shouldEqual ((Person("John", 30), None))
  }

  test("extracts for Optional (predicate is true)"){
    val oldPerson = Person("John", 100)
    val update = _oldAge extracts (_ * 2)

    update.run(oldPerson).value shouldEqual ((Person("John", 100), Some(200)))
  }

  val _nameGet = Getter[Person, String](_.name)

  test("extract for Getter"){
    val name = _nameGet extract

    name.run(p).value shouldEqual ((Person("John", 30), "John"))
  }

  test("extracts for Getter"){
    val upper = _nameGet extracts (_.toUpperCase)

    upper.run(p).value shouldEqual ((Person("John", 30), "JOHN"))
  }
}
