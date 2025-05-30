package monocle.focus

import monocle.*
import monocle.syntax.all.*
import monocle.syntax.{AppliedGetter, AppliedSetter}

object ComposedFocusTest {
  enum Roof {
    case Tiles(numTiles: Int)
    case Thatch(color: Color)
    case Glass(tint: Option[String])
  }

  case class Color(r: Int, g: Int, b: Int)

  case class Mailbox(address: Address)
  case class User(name: String, address: Address)
  case class Street(name: String)
  case class Potato(count: Int)
  case class Address(streetNumber: Int, street: Option[Street], roof: Roof, potatoes: List[Potato])

  case class MailingList(users: List[User])

  val elise =
    User("Elise", Address(12, Some(Street("high street")), Roof.Tiles(999), (1 to 4).toList.map(Potato.apply)))
  val mailbox = Mailbox(Address(1, Some(Street("cherrytree lane")), Roof.Thatch(Color(255, 255, 0)), Nil))
}

final class ComposedFocusTest extends munit.FunSuite {

  import ComposedFocusTest.*

  test("Lens refocus correctly composes Lens") {
    val addressLens: Lens[User, Address] = Focus[User](_.address)
    val newLens: Lens[User, Int]         = addressLens.refocus(_.streetNumber)
    val newElise                         = newLens.replace(50)(elise)

    assertEquals(newElise.address.streetNumber, 50)
  }

  test("AppliedLens refocus correctly composes Lens") {
    val addressLens: AppliedLens[User, Address] = elise.focus(_.address)
    val newLens: AppliedLens[User, Int]         = addressLens.refocus(_.streetNumber)
    val newElise                                = newLens.replace(50)

    assertEquals(newElise.address.streetNumber, 50)
  }

  test("Lens refocus correctly composes Prism") {
    val roofLens: Lens[User, Roof]          = Focus[User](_.address.roof)
    val newLens: Optional[User, Roof.Tiles] = roofLens.refocus(_.as[Roof.Tiles])
    val newElise                            = newLens.replace(Roof.Tiles(3))(elise)

    assertEquals(newElise.address.roof, Roof.Tiles(3))
  }

  test("Lens refocus correctly composes Iso") {
    val addressLens: Lens[User, Address] = Focus[User](_.address)
    val newLens: Lens[User, Int]         = addressLens.refocus(_.streetNumber)
    val newElise                         = newLens.replace(50)(elise)

    assertEquals(newElise.address.streetNumber, 50)
  }

  test("Lens refocus correctly composes Optional") {
    val addressLens: Lens[User, Address] = Focus[User](_.address)
    val newLens: Optional[User, String]  = addressLens.refocus(_.street.some.name)
    val newElise                         = newLens.replace("Crunkley Ave")(elise)

    assertEquals(newElise.address.street.map(_.name), Some("Crunkley Ave"))
  }

  test("Lens refocus correctly composes Traversal") {
    val addressLens: Lens[User, Address] = Focus[User](_.address)
    val newLens: Traversal[User, Int]    = addressLens.refocus(_.potatoes.each.count)
    val newElise                         = newLens.modify(_ + 1)(elise)

    assertEquals(newElise.address.potatoes.map(_.count), List(2, 3, 4, 5))
  }

  test("Prism refocus correctly composes Lens") {
    val oldLens: Prism[Roof, Roof.Thatch] = Focus[Roof](_.as[Roof.Thatch])
    val newLens: Optional[Roof, Int]      = oldLens.refocus(_.color.r)
    val newRoof                           = newLens.replace(77)(Roof.Thatch(Color(255, 255, 255)))

    assertEquals(newRoof, Roof.Thatch(Color(77, 255, 255)))
  }

  test("Prism refocus correctly composes Prism") {
    val oldLens: Prism[Roof, Roof.Glass] = Focus[Roof](_.as[Roof.Glass])
    val newLens: Prism[Roof, String]     = oldLens.refocus(_.tint.some)
    val newRoof                          = newLens.replace("light")(Roof.Glass(Some("dark")))

    assertEquals(newRoof, Roof.Glass(Some("light")))
  }

  test("Prism refocus correctly composes Iso") {
    val oldLens: Prism[Roof, Roof.Tiles] = Focus[Roof](_.as[Roof.Tiles])
    val newLens: Prism[Roof, Int]        = oldLens.refocus(_.numTiles)
    val newRoof                          = newLens.replace(100)(Roof.Tiles(3))

    assertEquals(newRoof, Roof.Tiles(100))
  }

  test("Prism refocus correctly composes Optional") {
    val oldLens: Prism[Roof, Roof.Glass] = Focus[Roof](_.as[Roof.Glass])
    val newLens: Optional[Roof, String]  = oldLens.refocus(_.tint.some)
    val newRoof                          = newLens.replace("light")(Roof.Glass(Some("dark")))

    assertEquals(newRoof, Roof.Glass(Some("light")))
  }

  test("Fold refocus correctly composes Lens") {
    val userFold: Fold[MailingList, Address] =
      Focus[MailingList](_.users.each).andThen(Getter[User, Address](_.address))
    val newLens: Fold[MailingList, Int] = userFold.refocus(_.streetNumber)
    val streetNumbers                   = newLens.getAll(MailingList(List(elise)))

    assertEquals(streetNumbers, List(12))
  }

  test("AppliedFold refocus correctly composes Lens") {
    val mailingList                                 = MailingList(List(elise))
    val userFold: AppliedFold[MailingList, Address] =
      mailingList.focus(_.users.each).andThen(Getter[User, Address](_.address))
    val newLens: AppliedFold[MailingList, Int] = userFold.refocus(_.streetNumber)
    val streetNumbers                          = newLens.getAll

    assertEquals(streetNumbers, List(12))
  }

  test("Getter refocus correctly composes Lens") {
    val addressLens: Getter[User, Address] = Getter[User, Address](_.address)
    val newLens: Getter[User, Int]         = addressLens.refocus(_.streetNumber)
    val streetNumber                       = newLens.get(elise)

    assertEquals(streetNumber, 12)
  }

  test("AppliedGetter refocus correctly composes Lens") {
    val addressLens: AppliedGetter[User, Address] = AppliedGetter(elise, Getter[User, Address](_.address))
    val newLens: AppliedGetter[User, Int]         = addressLens.refocus(_.streetNumber)
    val streetNumber                              = newLens.get

    assertEquals(streetNumber, 12)
  }

  test("Setter refocus correctly composes Lens") {
    val addressLens: Setter[User, Address] = Setter(f => user => user.copy(address = f(user.address)))
    val newLens: Setter[User, Int]         = addressLens.refocus(_.streetNumber)
    val newElise                           = newLens.replace(50)(elise)

    assertEquals(newElise.address.streetNumber, 50)
  }

  test("AppliedSetter refocus correctly composes Lens") {
    val addressLens: AppliedSetter[User, Address] =
      AppliedSetter(elise, Setter(f => user => user.copy(address = f(user.address))))
    val newLens: AppliedSetter[User, Int] = addressLens.refocus(_.streetNumber)
    val newElise                          = newLens.replace(50)

    assertEquals(newElise.address.streetNumber, 50)
  }
}
