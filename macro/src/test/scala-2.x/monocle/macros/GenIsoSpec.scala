package monocle.macros

import cats.Eq
import munit.DisciplineSuite
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import monocle.law.discipline._

class GenIsoSpec extends DisciplineSuite {

  case object AnObject
  implicit val anObjectGen: Arbitrary[AnObject.type] = Arbitrary(Gen.const(AnObject))
  implicit val anObjectEq                            = Eq.fromUniversalEquals[AnObject.type]

  case class IntWrapper(i: Int)
  implicit val intWrapperGen: Arbitrary[IntWrapper] = Arbitrary(arbitrary[Int].map(IntWrapper.apply))
  implicit val intWrapperEq: Eq[IntWrapper]         = Eq.fromUniversalEquals[IntWrapper]

  case class IdWrapper[A](value: A)
  implicit def idWrapperGen[A: Arbitrary]: Arbitrary[IdWrapper[A]] =
    Arbitrary(arbitrary[A].map(IdWrapper.apply))
  implicit def idWrapperEq[A: Eq]: Eq[IdWrapper[A]] = Eq.fromUniversalEquals

  case class EmptyCase()
  implicit val emptyCaseGen: Arbitrary[EmptyCase] = Arbitrary(Gen.const(EmptyCase()))
  implicit val emptyCaseEq: Eq[EmptyCase]         = Eq.fromUniversalEquals[EmptyCase]

  case class EmptyCaseType[A]()
  implicit def emptyCaseTypeGen[A]: Arbitrary[EmptyCaseType[A]] =
    Arbitrary(Gen.const(EmptyCaseType()))
  implicit def emptyCaseTypeEq[A]: Eq[EmptyCaseType[A]] = Eq.fromUniversalEquals[EmptyCaseType[A]]

  checkAll("GenIso", IsoTests(GenIso[IntWrapper, Int]))
  checkAll("GenIso with type param", IsoTests(GenIso[IdWrapper[Int], Int]))
  checkAll("GenIso.unit object", IsoTests(GenIso.unit[AnObject.type]))
  checkAll("GenIso.unit empty case class", IsoTests(GenIso.unit[EmptyCase]))
  checkAll("GenIso.unit empty case class with type param", IsoTests(GenIso.unit[EmptyCaseType[Int]]))

}
