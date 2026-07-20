package monocle.focus

import monocle.Focus
import monocle.Focus.*

final class NamedTupleAppliedFocusTest extends munit.FunSuite {

  test("Applied focus returning an Optional in nested named tuples") {
    // type User = (name: String, address: Option[Address])
    // type Address = (streetNumber: Int, postcode: String)
    // works when type of address is widened from Some to Option
    // the macro cannot find a matching overload otherwise - note that this also happens in normal focus on case classes when a type is described as Some instead of Option
    val elise = (name = "Elise", address = Option((streetNumber = 12, postcode = "high street")))

    val streetNumber = elise.focus(_.address.some.streetNumber).getOption
    val newElise     = elise.focus(_.address.some.streetNumber).replace(50)

    assertEquals(streetNumber, Some(12))
    assertEquals(newElise, (name = "Elise", address = Some((streetNumber = 50, postcode = "high street"))))
  }

  test("Applied focus returning an Optional with a named tuple inside a case class") {
    case class User(name: String, address: Option[Address])
    type Address = (streetNumber: Int, postcode: String)

    val elise = User("Elise", Some((streetNumber = 12, postcode = "high street")))


    val streetNumber = elise.focus(_.address.some.streetNumber).getOption
    val newElise     = elise.focus(_.address.some.streetNumber).replace(50)

    assertEquals(streetNumber, Some(12))
    assertEquals(newElise, User("Elise", Some((50, "high street"))))
  }

  test("Applied focus returning an Optional with a case class inside a named tuple") {
    case class Address(streetNumber: Int, postcode: String)

    val elise = (name = "Elise", address = Option(Address(12, "high street")))


    val streetNumber = elise.focus(_.address.some.streetNumber).getOption
    val newElise     = elise.focus(_.address.some.streetNumber).replace(50)

    assertEquals(streetNumber, Some(12))
    assertEquals(newElise, (name = "Elise", address = Some(Address(50, "high street"))))
  }


  test("Applied focus returning an Optional") {
    case class User(name: String, address: Option[Address])
    case class Address(streetNumber: Int, postcode: String)
    val elise = User("Elise", Some(Address(12, "high street")))

    val streetNumber = elise.focus(_.address.some.streetNumber).getOption
    val newElise     = elise.focus(_.address.some.streetNumber).replace(50)

    assertEquals(streetNumber, Some(12))
    assertEquals(newElise, User("Elise", Some(Address(50, "high street"))))
  }

  // test("Applied focus returning a Lens") {
  //   case class User(name: String, address: Address)
  //   case class Address(streetNumber: Int, postcode: String)
  //
  //   val bob = User("Bob", Address(5, "Bob St"))
  //
  //   val streetNumber = bob.focus(_.address.streetNumber).get
  //   val newBob       = bob.focus(_.address.streetNumber).replace(77)
  //
  //   assertEquals(streetNumber, 5)
  //   assertEquals(newBob, User("Bob", Address(77, "Bob St")))
  // }

  test("Applied focus returning am Optional with NamedTuple.From") {
    case class User[A](name: String, address: A)
    case class Address(streetNumber: Int, postcode: String)

    type Bob = NamedTuple.From[User[Option[NamedTuple.From[Address]]]]

    val bob: Bob = (
      name = "Bob",
      address = Option(streetNumber = 5, postcode = "Bob St")
    )

    val streetNumber = bob.focus(_.address.some.streetNumber).getOption
    val newBob       = bob.focus(_.address.some.streetNumber).replace(77)

    assertEquals(streetNumber, Some(5))
    assertEquals(newBob, (
      name = "Bob",
      address = Option(streetNumber = 77, postcode = "Bob St")
    ))
  }
}
