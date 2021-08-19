package monocle.focus

import monocle.{Focus, Iso}

final class FocusWithDefaultTest extends munit.FunSuite {

  test("Access `withDefault` directly on argument") {
    val iso: Iso[Option[Int], Int] = Focus[Option[Int]](_.withDefault(555))
    assertEquals(iso.get(Some(3)), 3)
    assertEquals(iso.get(None), 555)
  }

  test("Access `withDefault` on field") {
    case class User(name: String, address: Option[Address])
    case class Address(streetNumber: Int, postcode: String)

    val elise = User("Elise", Some(Address(12, "high street")))
    val bob   = User("bob", None)

    val streetNumber = Focus[User](_.address.withDefault(Address(333, "abc")).streetNumber)

    assertEquals(streetNumber.get(elise), 12)
    assertEquals(streetNumber.get(bob), 333)
  }

  test("Access `withDefault` on a parametric Option within a case class") {
    case class IdOpt[+A](id: Long, value: Option[A])
    case class User(name: String, age: Int)

    val bob    = User("bob", 24)
    val idSome = IdOpt(1, Some(bob))
    val idNone = IdOpt(1, None)

    val age = Focus[IdOpt[User]](_.value.withDefault(User("Grug", 99)).age)

    assertEquals(age.get(idSome), 24)
    assertEquals(age.get(idNone), 99)
  }

  test("Access `withDefault` on top level Option") {
    case class User(name: String, age: Int)
    val bob = User("bob", 24)

    val age = Focus[Option[User]](_.withDefault(User("Smee", 888)).age)

    assertEquals(age.get(Some(bob)), 24)
    assertEquals(age.get(None), 888)
  }

  test("Access `withDefault` on nested Option") {
    case class User(name: String, age: Int)
    val bob = User("Friedrich", 33)

    val name =
      Focus[Option[Option[User]]](_.withDefault(Some(User("Gunther", 22))).withDefault(User("Brunhild", 44)).name)

    assertEquals(name.get(Some(Some(bob))), "Friedrich")
    assertEquals(name.get(Some(None)), "Brunhild")
    assertEquals(name.get(None), "Gunther")
  }

  test("Focus operator `withDefault` commutes with standalone operator `withDefault`") {
    val opt: Option[Int] = Some(33)

    assertEquals(Focus[Option[Int]](_.withDefault(99)).get(opt), Focus[Option[Int]](a => a).withDefault(99).get(opt))
  }
}
