package monocle.focus

import monocle.Focus
import monocle.Focus._

final class FocusSomeTest extends munit.FunSuite {

  test("Access Option within a case class") {
    case class User(name: String, address: Option[Address])
    case class Address(streetNumber: Int, postcode: String)

    val elise = User("Elise", Some(Address(12, "high street")))
    val bob   = User("bob", None)

    val streetNumber = Focus[User](_.address.some.streetNumber)

    assertEquals(streetNumber.getOption(elise), Some(12))
    assertEquals(streetNumber.getOption(bob), None)
  }

  test("Access parametric Option within a case class") {
    case class IdOpt[+A](id: Long, value: Option[A])
    case class User(name: String, age: Int)

    val bob    = User("bob", 24)
    val idSome = IdOpt(1, Some(bob))
    val idNone = IdOpt(1, None)

    val age = Focus[IdOpt[User]](_.value.some.age)

    assertEquals(age.getOption(idSome), Some(24))
    assertEquals(age.getOption(idNone), None)
  }

  test("Access top level Option") {
    case class User(name: String, age: Int)
    val bob = User("bob", 24)

    val age = Focus[Option[User]](_.some.age)

    assertEquals(age.getOption(Some(bob)), Some(24))
    assertEquals(age.getOption(None), None)
  }

  test("Access nested Option") {
    case class User(name: String, age: Int)
    val bob = User("Friedrich", 33)

    val name = Focus[Option[Option[User]]](_.some.some.name)

    assertEquals(name.getOption(Some(Some(bob))), Some("Friedrich"))
    assertEquals(name.getOption(Some(None)), None)
    assertEquals(name.getOption(None), None)
  }

  test("Focus operator `some` commutes with standalone operator `some`") {

    val opt: Option[Int] = Some(33)

    assertEquals(Focus[Option[Int]](_.some).getOption(opt), Focus[Option[Int]](a => a).some.getOption(opt))
  }
}
