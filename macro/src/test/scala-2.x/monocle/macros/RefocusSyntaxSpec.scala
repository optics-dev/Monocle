package monocle.macros

import monocle._
import monocle.syntax.AppliedIso
import monocle.syntax.all._
import munit.DisciplineSuite

class RefocusSyntaxSpec extends DisciplineSuite {

  case class User(name: String, address: Address)
  case class Address(streetNumber: Int, postcode: String)

  val iso: Iso[User, User]             = Iso.id
  val lens: Lens[User, User]           = Iso.id
  val prism: Prism[User, User]         = Iso.id
  val optional: Optional[User, User]   = Iso.id
  val traversal: Traversal[User, User] = Iso.id
  val setter: Setter[User, User]       = Iso.id
  val getter: Getter[User, User]       = Iso.id
  val fold: Fold[User, User]           = Iso.id

  val user = User("Bob", Address(12, "EC1 7RT"))

  test("iso.refocus")(assertEquals(iso.refocus(_.name).get(user), user.name))
  test("lens.refocus")(assertEquals(lens.refocus(_.name).get(user), user.name))
  test("prism.refocus")(assertEquals(prism.refocus(_.name).getOption(user), Some(user.name)))
  test("optional.refocus")(assertEquals(optional.refocus(_.name).getOption(user), Some(user.name)))
  test("traversal.refocus")(assertEquals(traversal.refocus(_.name).getAll(user), List(user.name)))
  test("setter.refocus")(assertEquals(setter.refocus(_.name).replace("Eda")(user), user.copy(name = "Eda")))
  test("getter.refocus")(assert(getter.refocus(_.name).get(user) == user.name))
  test("fold.refocus")(assert(fold.refocus(_.name).getAll(user) == List(user.name)))

  val appliedIso: AppliedIso[User, User]             = AppliedIso(user, iso)
  val appliedLens: AppliedLens[User, User]           = AppliedIso(user, iso)
  val appliedPrism: AppliedPrism[User, User]         = AppliedIso(user, iso)
  val appliedOptional: AppliedOptional[User, User]   = AppliedIso(user, iso)
  val appliedTraversal: AppliedTraversal[User, User] = AppliedIso(user, iso)
  val appliedSetter: AppliedSetter[User, User]       = AppliedIso(user, iso)
  val appliedGetter: AppliedGetter[User, User]       = AppliedIso(user, iso)
  val appliedFold: AppliedFold[User, User]           = AppliedIso(user, iso)

  test("applied iso.refocus")(assertEquals(appliedIso.refocus(_.name).get, user.name))
  test("applied lens.refocus")(assertEquals(appliedLens.refocus(_.name).get, user.name))
  test("applied prism.refocus")(assertEquals(appliedPrism.refocus(_.name).getOption, Some(user.name)))
  test("applied optional.refocus")(assertEquals(appliedOptional.refocus(_.name).getOption, Some(user.name)))
  test("applied traversal.refocus")(assertEquals(appliedTraversal.refocus(_.name).getAll, List(user.name)))
  test("applied setter.refocus")(assertEquals(appliedSetter.refocus(_.name).replace("Eda"), user.copy(name = "Eda")))
  test("applied getter.refocus")(assert(appliedGetter.refocus(_.name).get == user.name))
  test("applied fold.refocus")(assert(appliedFold.refocus(_.name).getAll == List(user.name)))

}
