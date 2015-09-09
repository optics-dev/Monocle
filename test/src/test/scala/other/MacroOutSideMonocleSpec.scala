package other

import monocle.MonocleSuite
import monocle.law.discipline.{IsoTests, LensTests, PrismTests}
import monocle.macros.{GenIso, GenLens, GenPrism}
import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}

import scalaz.Equal

class MacroOutSideMonocleSpec extends MonocleSuite {

  case class Example(i: Int)
  case object ExampleObject
  case class EmptyCase()
  case class EmptyCaseType[A]()

  sealed trait Foo
  case class Bar1(s: String) extends Foo
  case class Bar2(i: Int) extends Foo

  implicit val exampleArb: Arbitrary[Example] = Arbitrary(arbitrary[Int].map(Example.apply))
  implicit val exampleObjArb: Arbitrary[ExampleObject.type] = Arbitrary(Gen.const(ExampleObject))
  implicit val emptyCaseArb: Arbitrary[EmptyCase] = Arbitrary(Gen.const(EmptyCase()))
  implicit def emptyCaseTypeArb[A]: Arbitrary[EmptyCaseType[A]] = Arbitrary(Gen.const(EmptyCaseType()))
  implicit val bar1Arb: Arbitrary[Bar1] = Arbitrary(arbitrary[String].map(Bar1.apply))
  implicit val bar2Arb: Arbitrary[Bar2] = Arbitrary(arbitrary[Int].map(Bar2.apply))
  implicit val fooArb: Arbitrary[Foo] = Arbitrary(Gen.oneOf(arbitrary[Bar1], arbitrary[Bar2]))

  implicit val exampleEq: Equal[Example] = Equal.equalA[Example]
  implicit val exampleObjEq: Equal[ExampleObject.type] = Equal.equalA[ExampleObject.type]
  implicit val emptyCaseEq: Equal[EmptyCase] = Equal.equalA[EmptyCase]
  implicit def emptyCaseTypeEq[A]: Equal[EmptyCaseType[A]] = Equal.equalA[EmptyCaseType[A]]
  implicit val bar1Eq: Equal[Bar1] = Equal.equalA[Bar1]
  implicit val fooEq: Equal[Foo] = Equal.equalA[Foo]

  checkAll("GenIso"                                       , IsoTests(GenIso[Example, Int]))
  checkAll("GenIso.unit object"                           , IsoTests(GenIso.unit[ExampleObject.type]))
  checkAll("GenIso.unit empty case class"                 , IsoTests(GenIso.unit[EmptyCase]))
  checkAll("GenIso.unit empty case class with type param" , IsoTests(GenIso.unit[EmptyCaseType[Int]]))
  checkAll("GenLens"                                      , LensTests(GenLens[Example](_.i)))
  checkAll("GenPrism"                                     , PrismTests(GenPrism[Foo, Bar1]))
}
