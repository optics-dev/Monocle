package monocle.focus

import monocle.Focus
import monocle.Focus._
import monocle._

final class ComposedFocusTest extends munit.FunSuite {

  case class Mailbox(address: Address)
  case class User(name: String, address: Address)
  case class Address(streetNumber: Int, postcode: String)

  val elise = User("Elise", Address(12, "high street"))
  val mailbox = Mailbox(Address(1, "cherrytree lane"))


  test("Lens refocus correctly composes") {
    val addressLens: Lens[User, Address] = Focus[User](_.address)
    val newElise = addressLens.refocus(_.streetNumber).replace(50)(elise)
    assertEquals(newElise, User("Elise", Address(50, "high street")))
  }

  // test("Applied lens refocus correctly composes") {
  //   val addressLens: AppliedLens[User, Address] = elise.focus(_.address)
  //   val newElise = addressLens.refocus(_.streetNumber).replace(50)
  //   assertEquals(newElise, User("Elise", Address(50, "high street")))
  // }

  test("Iso refocus correctly composes") {
    val addressLens: Iso[Mailbox, Address] = Focus[Mailbox](_.address)
    val newMailbox = addressLens.refocus(_.streetNumber).replace(7)(mailbox)
    assertEquals(newMailbox, Mailbox(Address(7, "cherrytree lane")))
  }

  // test("Applied Iso refocus correctly composes") {
  //   val addressLens: AppliedLens[User, Address] = elise.focus(_.address)
  //   val newElise = addressLens.refocus(_.streetNumber).replace(50)
  //   assertEquals(newElise, User("Elise", Address(50, "high street")))
  // }
}
