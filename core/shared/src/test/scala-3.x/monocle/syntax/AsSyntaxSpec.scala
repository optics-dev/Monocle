package monocle.syntax

import monocle._
import monocle.syntax.all._
import munit.DisciplineSuite

class AsSyntaxSpec extends DisciplineSuite {

  sealed trait StringOrInt
  case class S(value: String) extends StringOrInt
  case class I(value: Int) extends StringOrInt

  val iso: Iso[StringOrInt, StringOrInt]             = Iso.id
  val lens: Lens[StringOrInt, StringOrInt]           = Iso.id
  val prism: Prism[StringOrInt, StringOrInt]         = Iso.id
  val optional: Optional[StringOrInt, StringOrInt]   = Iso.id
  val traversal: Traversal[StringOrInt, StringOrInt] = Iso.id
  val setter: Setter[StringOrInt, StringOrInt]       = Iso.id
  val getter: Getter[StringOrInt, StringOrInt]       = Iso.id
  val fold: Fold[StringOrInt, StringOrInt]           = Iso.id

  test("iso.as"){ assertEquals(iso.as[I].getOption(I(1)), Some(I(1))) }
  test("lens.as"){ assertEquals(lens.as[I].getOption(I(1)), Some(I(1))) }
  test("prism.as"){ assertEquals(prism.as[I].getOption(I(1)), Some(I(1))) }
  test("optional.as"){ assertEquals(optional.as[I].getOption(I(1)), Some(I(1))) }
  test("traversal.as"){ assertEquals(traversal.as[I].getAll(I(1)), List(I(1))) }
  test("setter.as"){ assertEquals(setter.as[I].replace(I(5))(I(1)), I(5)) }
  test("getter.as"){ assert(getter.as[I].getAll(I(1)) == List(I(1))) }
  test("fold.as"){ assert(fold.as[I].getAll(I(1)) == List(I(1))) }

  val sOrI: StringOrInt = I(1)
  val appliedIso: AppliedIso[StringOrInt, StringOrInt]             = AppliedIso(sOrI, iso)
//  val appliedLens: AppliedLens[StringOrInt, StringOrInt]           = AppliedIso(sOrI, iso)
//  val appliedPrism: AppliedPrism[StringOrInt, StringOrInt]         = AppliedIso(sOrI, iso)
//  val appliedOptional: AppliedOptional[StringOrInt, StringOrInt]   = AppliedIso(sOrI, iso)
//  val appliedTraversal: AppliedTraversal[StringOrInt, StringOrInt] = AppliedIso(sOrI, iso)
//  val appliedSetter: AppliedSetter[StringOrInt, StringOrInt]       = AppliedIso(sOrI, iso)
//  val appliedGetter: AppliedGetter[StringOrInt, StringOrInt]       = AppliedIso(sOrI, iso)
//  val appliedFold: AppliedFold[StringOrInt, StringOrInt]           = AppliedIso(sOrI, iso)

  test("applied iso.as"){ assertEquals(appliedIso.as[I].getOption, Some(I(1))) }
//  test("applied lens.as"){ assertEquals(appliedLens.as[I].getOption, Some(I(1))) }
//  test("applied prism.as"){ assertEquals(appliedPrism.as[I].getOption, Some(I(1))) }
//  test("applied optional.as"){ assertEquals(appliedOptional.as[I].getOption, Some(I(1))) }
//  test("applied traversal.as"){ assertEquals(appliedTraversal.as[I].getAll, List(I(1))) }
//  test("applied setter.as"){ assertEquals(appliedSetter.as[I].replace(I(5)), I(5)) }
//  test("applied getter.as"){ assert(appliedGetter.as[I].getAll == List(I(1))) }
//  test("applied fold.as"){ assert(appliedFold.as[I].getAll == List(I(1))) }

}
