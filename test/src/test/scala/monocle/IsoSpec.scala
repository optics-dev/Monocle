package monocle

import monocle.law._
import monocle.macros.GenIso
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.specs2.scalaz.Spec

import scalaz.Equal
import scalaz.std.anyVal._


class IsoSpec extends Spec {

  case class IntWrapper(i: Int)

  implicit val intWrapperGen: Arbitrary[IntWrapper] = Arbitrary(arbitrary[Int].map(IntWrapper.apply))

  implicit val intWrapperEq = Equal.equalA[IntWrapper]

  val iso = Iso[IntWrapper, Int](_.i)(IntWrapper.apply)

  checkAll("apply Iso", IsoLaws(iso))
  checkAll("GenIso", IsoLaws(GenIso[IntWrapper, Int]))

  checkAll("Iso id", IsoLaws(Iso.id[Int]))

  checkAll("Iso.asLens"     , LensLaws(iso.asLens))
  checkAll("Iso.asPrism"    , PrismLaws(iso.asPrism))
  checkAll("Iso.asOptional" , OptionalLaws(iso.asOptional))
  checkAll("Iso.asTraversal", TraversalLaws(iso.asTraversal))
  checkAll("Iso.asSetter"   , SetterLaws(iso.asSetter))

}

