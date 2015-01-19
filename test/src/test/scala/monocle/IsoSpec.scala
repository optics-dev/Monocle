package monocle

import monocle.law.IsoLaws
import monocle.macros.Isoer
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.specs2.scalaz.Spec

import scalaz.Equal
import scalaz.std.anyVal._


class IsoSpec extends Spec {

  case class IntWrapper(i: Int)

  implicit val intWrapperGen: Arbitrary[IntWrapper] = Arbitrary(arbitrary[Int].map(IntWrapper.apply))

  implicit val intWrapperEq = Equal.equalA[IntWrapper]

  checkAll("macro iso", IsoLaws(Isoer[IntWrapper, Int]))
  checkAll("apply iso", IsoLaws(Iso[IntWrapper, Int](_.i)(IntWrapper.apply)))

  checkAll("iso id", IsoLaws(Iso.id[Int]))

}

