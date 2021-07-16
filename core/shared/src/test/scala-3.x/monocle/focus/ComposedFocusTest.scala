package monocle.focus

import monocle.Focus
import monocle.Focus._
import monocle._


final class ComposedFocusTest extends munit.FunSuite {

  enum Roof {
    case Tiles(numTiles: Int)
    case Thatch(color: String)
  }

  case class Mailbox(address: Address)
  case class User(name: String, address: Address)
  case class Address(streetNumber: Int, postcode: String, roof: Roof)

  val elise = User("Elise", Address(12, "high street", Roof.Tiles(999)))
  val mailbox = Mailbox(Address(1, "cherrytree lane", Roof.Thatch("yellow")))

  test("Lens refocus correctly composes Lens") {
    val addressLens: Lens[User, Address] = Focus[User](_.address)
    val newLens: Lens[User, Int] = addressLens.refocus(_.streetNumber)
    val newElise = addressLens.refocus(_.streetNumber).replace(50)(elise)

    assertEquals(newElise.address.streetNumber, 50)
  }

  test("Lens refocus correctly composes Prism") {
    val roofLens: Lens[User, Roof] = Focus[User](_.address.roof)
    val newLens: Optional[User, Roof.Tiles] = addressLens.refocus(_.as[Roof.Tiles])
    val newElise = newLens.replace(Roof.Tiles(3))(elise)
    assertEquals(newElise.address.roof, Roof.Tiles(3))
  }

  test("Lens refocus correctly composes Iso") {
    val addressLens: Lens[User, Address] = Focus[User](_.address)
    val newLens: Lens[User, Int] = addressLens.refocus(_.streetNumber)
    val newElise = addressLens.refocus(_.streetNumber).replace(50)(elise)

    assertEquals(newElise.address.streetNumber, 50)
  }
/*
  test("Applied lens refocus correctly composes") {
    val addressLens: AppliedLens[User, Address] = elise.focus(_.address)
    val newLens = addressLens.refocus(_.streetNumber)

    assertEquals(newLens.replace(50), User("Elise", Address(50, "high street")))
  }

  test("Iso refocus correctly composes") {
    val addressLens: Iso[Mailbox, Address] = Focus[Mailbox](_.address)
    val newMailbox = addressLens.refocus(_.streetNumber).replace(7)(mailbox)
    assertEquals(newMailbox, Mailbox(Address(7, "cherrytree lane")))
  }

  test("Applied Iso refocus correctly composes") {
    val addressLens: AppliedLens[User, Address] = elise.focus(_.address)
    val newElise = addressLens.refocus(_.streetNumber).replace(50)
    assertEquals(newElise, User("Elise", Address(50, "high street")))
  }*/
}
