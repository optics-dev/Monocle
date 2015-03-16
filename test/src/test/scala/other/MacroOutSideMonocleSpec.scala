package other

import monocle.law.{PrismLaws, IsoLaws, LensLaws}
import monocle.macros.{GenPrism, GenIso, GenLens}
import org.scalacheck.{Gen, Arbitrary}
import org.scalacheck.Arbitrary._
import org.specs2.scalaz.Spec
import scalaz.std.anyVal._

import scalaz.Equal

class MacroOutSideMonocleSpec extends Spec {

  case class Example(i: Int)

  sealed trait Foo
  case class Bar1(s: String) extends Foo
  case class Bar2(i: Int) extends Foo

  implicit val exampleArb: Arbitrary[Example] = Arbitrary(arbitrary[Int].map(Example.apply))
  implicit val bar1Arb: Arbitrary[Bar1] = Arbitrary(arbitrary[String].map(Bar1.apply))
  implicit val bar2Arb: Arbitrary[Bar2] = Arbitrary(arbitrary[Int].map(Bar2.apply))
  implicit val fooArb: Arbitrary[Foo] = Arbitrary(Gen.oneOf(arbitrary[Bar1], arbitrary[Bar2]))

  implicit val exampleEq: Equal[Example] = Equal.equalA[Example]
  implicit val bar1Eq: Equal[Bar1] = Equal.equalA[Bar1]
  implicit val fooEq: Equal[Foo] = Equal.equalA[Foo]


  checkAll("GenIso"  , IsoLaws(GenIso[Example, Int]))
  checkAll("GenLens" , LensLaws(GenLens[Example](_.i)))
  checkAll("GenPrism", PrismLaws(GenPrism[Foo, Bar1]))

}
