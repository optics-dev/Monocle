package monocle.focus

import monocle.Focus
import monocle.Focus.*

final class AppliedFocusTest extends munit.FunSuite {

  test("Applied focus returning an Optional") {
    case class User(name: String, address: Option[Address])
    case class Address(streetNumber: Int, postcode: String)

    val elise = User("Elise", Some(Address(12, "high street")))

    val streetNumber = elise.focus(_.address.some.streetNumber).getOption
    val newElise     = elise.focus(_.address.some.streetNumber).replace(50)

    assertEquals(streetNumber, Some(12))
    assertEquals(newElise, User("Elise", Some(Address(50, "high street"))))
  }

  test("Applied focus returning a Lens") {
    case class User(name: String, address: Address)
    case class Address(streetNumber: Int, postcode: String)

    val bob = User("Bob", Address(5, "Bob St"))

    val streetNumber = bob.focus(_.address.streetNumber).get
    val newBob       = bob.focus(_.address.streetNumber).replace(77)

    assertEquals(streetNumber, 5)
    assertEquals(newBob, User("Bob", Address(77, "Bob St")))
  }
}
