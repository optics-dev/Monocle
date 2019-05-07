package monocle.state

import cats.Applicative
import cats.implicits._
import monocle.{Getter, MonocleSuite, Optional, PTraversal, Setter}
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

  test("mod"){
    val increment = for {
      i <- _age mod (_ + 1)
    } yield i

    increment.run(p).value shouldEqual ((Person("John", 31), 31))
  }

  test("modo"){
    val increment = for {
      i <- _age modo (_ + 1)
    } yield i

    increment.run(p).value shouldEqual ((Person("John", 31), 30))
  }

  test("mod_"){
    val increment = _age mod_ (_ + 1)

    increment.run(p).value shouldEqual ((Person("John", 31), ()))
  }

  test("assign"){
    val set20 = for {
      i <- _age assign 20
    } yield i

    set20.run(p).value shouldEqual ((Person("John", 20), 20))
  }

  test("assigno"){
    val set20 = for {
      i <- _age assigno 20
    } yield i

    set20.run(p).value shouldEqual ((Person("John", 20), 30))
  }

  test("assign_"){
    val set20 = _age assign_ 20

    set20.run(p).value shouldEqual ((Person("John", 20), ()))
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

  test("mod for Optional (predicate is false)"){
    val youngPerson = Person("John", 30)
    val update = for {
      i <- _oldAge mod (_ + 1)
    } yield i

    update.run(youngPerson).value shouldEqual ((Person("John", 30), None))
  }

  test("mod for Optional (predicate is true)"){
    val oldPerson = Person("John", 100)
    val update = for {
      i <- _oldAge mod (_ + 1)
    } yield i

    update.run(oldPerson).value shouldEqual ((Person("John", 101), Some(101)))
  }

  test("modo for Optional (predicate is false)"){
    val youngPerson = Person("John", 30)
    val update = for {
      i <- _oldAge modo (_ + 1)
    } yield i

    update.run(youngPerson).value shouldEqual ((Person("John", 30), None))
  }

  test("modo for Optional (predicate is true)"){
    val oldPerson = Person("John", 100)
    val update = for {
      i <- _oldAge modo (_ + 1)
    } yield i

    update.run(oldPerson).value shouldEqual ((Person("John", 101), Some(100)))
  }

  test("modo for Optional (chaining modifications)"){
    val oldCoolPerson = Person("Chris", 100)
    val update = for {
      _ <- _oldAge modo (_ + 1)
      x <- _coolGuy modo (_.toLowerCase)
    } yield x

    update.run(oldCoolPerson).value shouldEqual ((Person("chris", 101), Some("Chris")))
  }

  test("modo for Optional (only some of the modifications are applied)"){
    val oldCoolPerson = Person("Chris", 30)
    val update = for {
      _ <- _oldAge modo (_ + 1)
      x <- _coolGuy modo (_.toLowerCase)
    } yield x

    update.run(oldCoolPerson).value shouldEqual ((Person("chris", 30), Some("Chris")))
  }

  test("mod_ for Optional (predicate is false)"){
    val youngPerson = Person("John", 30)
    val update = _oldAge mod_ (_ + 1)

    update.run(youngPerson).value shouldEqual ((Person("John", 30), ()))
  }

  test("mod_ for Optional (predicate is true)"){
    val oldPerson = Person("John", 100)
    val update = _oldAge mod_ (_ + 1)

    update.run(oldPerson).value shouldEqual ((Person("John", 101), ()))
  }

  test("assign for Optional (predicate is true)"){
    val oldPerson = Person("John", 100)
    val update = for {
      i <- _oldAge assign 30
    } yield i

    update.run(oldPerson).value shouldEqual ((Person("John", 30), Some(30)))
  }

  test("assign for Optional (predicate is false)"){
    val youngPerson = Person("John", 30)
    val update = for {
      i <- _oldAge assign 100
    } yield i

    update.run(youngPerson).value shouldEqual ((Person("John", 30), None))
  }

  test("assigno for Optional (predicate is true)"){
    val oldPerson = Person("John", 100)
    val update = for {
      i <- _oldAge assigno 30
    } yield i

    update.run(oldPerson).value shouldEqual ((Person("John", 30), Some(100)))
  }

  test("assigno for Optional (predicate is false)"){
    val youngPerson = Person("John", 30)
    val update = for {
      i <- _oldAge assigno 100
    } yield i

    update.run(youngPerson).value shouldEqual ((Person("John", 30), None))
  }

  test("assign_ for Optional (predicate is true)"){
    val oldPerson = Person("John", 100)
    val update = _oldAge assign_ 30

    update.run(oldPerson).value shouldEqual ((Person("John", 30),()))
  }

  test("assign_ for Optional (predicate is false)"){
    val youngPerson = Person("John", 30)
    val update = _oldAge assign_ 100

    update.run(youngPerson).value shouldEqual ((Person("John", 30), ()))
  }

  val _nameSet = Setter[Person, String](f => p => p.copy(name = f(p.name)))

  test("mod_ for Setter"){
    val toUpper = _nameSet mod_ (_.toUpperCase)

    toUpper.run(p).value shouldEqual ((Person("JOHN", 30), ()))
  }

  test("assign_ for Setter"){
    val toUpper = _nameSet assign_ ("Juan")

    toUpper.run(p).value shouldEqual ((Person("Juan", 30), ()))
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

  // first and second projections of a triple
  def _pi12Tr[A, B, C]: PTraversal[(A, A, C), (B, B, C), A, B] =
    new PTraversal[(A, A, C), (B, B, C), A, B] {
      override def modifyF[F[_] : Applicative](f: A => F[B])(s: (A, A, C)): F[(B, B, C)] = {
        val (a1, a2, c) = s
        (f(a1), f(a2), c.pure[F]).tupled
      }
    }

  test("extract for Traversal"){
    val both = _pi12Tr[Int, Int, Char].extract

    both.run((1, 2, 'a')).value shouldEqual (((1, 2, 'a'), List(1, 2)))
  }

  test("extracts for Traversal"){
    val sum = _pi12Tr[Int, Int, Char].extracts(_.sum)

    sum.run((1, 2, 'a')).value shouldEqual (((1, 2, 'a'), 3))
  }

  test("mod for Traversal"){
    val len = _pi12Tr[String, Int, Char].mod(_.length)

    len.run(("john", "doe", 'a')).value shouldEqual (((4, 3, 'a'), List(4, 3)))
  }

  test("modF for Traversal"){
    val len = _pi12Tr[String, Int, Char].modF[Option]{ s =>
      val len = s.length
      if (len > 0) Some(len) else None
    }

    len.run(("john", "doe", 'a')).get shouldEqual (((4, 3, 'a'), List(4, 3)))
  }

  test("modo for Traversal"){
    val len = _pi12Tr[String, Int, Char].modo(_.length)

    len.run(("john", "doe", 'a')).value shouldEqual (((4, 3, 'a'), List("john", "doe")))
  }

  test("mod_ for Traversal"){
    val len = _pi12Tr[String, Int, Char].mod_(_.length)

    len.run(("john", "doe", 'a')).value shouldEqual (((4, 3, 'a'), ()))
  }

  test("assign for Traversal"){
    val toTrue = _pi12Tr[Int, Boolean, Char].assign(true)

    toTrue.run((1, 2, 'a')).value shouldEqual (((true, true, 'a'), List(true, true)))
  }

  test("assigno for Traversal"){
    val toTrue = _pi12Tr[Int, Boolean, Char].assigno(true)

    toTrue.run((1, 2, 'a')).value shouldEqual (((true, true, 'a'), List(1, 2)))
  }

  test("assign_ for Traversal"){
    val toTrue = _pi12Tr[Int, Boolean, Char].assign_(true)

    toTrue.run((1, 2, 'a')).value shouldEqual (((true, true, 'a'), ()))
  }
}
