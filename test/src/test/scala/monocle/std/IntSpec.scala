package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.{LensLaws, PrismLaws}
import org.specs2.scalaz.Spec

class IntSpec extends Spec {

  checkAll("atBit Int", LensLaws(atBit[Int](0)))

  checkAll("Int to Boolean", PrismLaws(intToBoolean))
  checkAll("Int to Byte"   , PrismLaws(intToByte))
  checkAll("Int to Char"   , PrismLaws(intToChar))

}
