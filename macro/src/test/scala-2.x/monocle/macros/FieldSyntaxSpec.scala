package monocle.macros

import monocle._
import monocle.syntax.AppliedIso
import monocle.syntax.all._
import munit.DisciplineSuite

class FieldSyntaxSpec extends DisciplineSuite {

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

  test("iso.field")(assertEquals(iso.field(_.name).get(user), user.name))
  test("lens.field")(assertEquals(lens.field(_.name).get(user), user.name))
  test("prism.field")(assertEquals(prism.field(_.name).getOption(user), Some(user.name)))
  test("optional.field")(assertEquals(optional.field(_.name).getOption(user), Some(user.name)))
  test("traversal.field")(assertEquals(traversal.field(_.name).getAll(user), List(user.name)))
  test("setter.field")(assertEquals(setter.field(_.name).replace("Eda")(user), user.copy(name = "Eda")))
  test("getter.field")(assert(getter.field(_.name).get(user) == user.name))
  test("fold.field")(assert(fold.field(_.name).getAll(user) == List(user.name)))

  val appliedIso: AppliedIso[User, User]             = AppliedIso(user, iso)
  val appliedLens: AppliedLens[User, User]           = AppliedIso(user, iso)
  val appliedPrism: AppliedPrism[User, User]         = AppliedIso(user, iso)
  val appliedOptional: AppliedOptional[User, User]   = AppliedIso(user, iso)
  val appliedTraversal: AppliedTraversal[User, User] = AppliedIso(user, iso)
  val appliedSetter: AppliedSetter[User, User]       = AppliedIso(user, iso)
  val appliedGetter: AppliedGetter[User, User]       = AppliedIso(user, iso)
  val appliedFold: AppliedFold[User, User]           = AppliedIso(user, iso)

  test("applied iso.field")(assertEquals(appliedIso.field(_.name).get, user.name))
  test("applied lens.field")(assertEquals(appliedLens.field(_.name).get, user.name))
  test("applied prism.field")(assertEquals(appliedPrism.field(_.name).getOption, Some(user.name)))
  test("applied optional.field")(assertEquals(appliedOptional.field(_.name).getOption, Some(user.name)))
  test("applied traversal.field")(assertEquals(appliedTraversal.field(_.name).getAll, List(user.name)))
  test("applied setter.field")(assertEquals(appliedSetter.field(_.name).replace("Eda"), user.copy(name = "Eda")))
  test("applied getter.field")(assert(appliedGetter.field(_.name).get == user.name))
  test("applied fold.field")(assert(appliedFold.field(_.name).getAll == List(user.name)))

}
