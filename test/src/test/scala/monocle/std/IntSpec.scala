package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.{OptionalLaws, PrismLaws}
import org.specs2.scalaz.Spec

class IntSpec extends Spec {

  checkAll("Int index bit", OptionalLaws(index[Int, Int, Boolean](0)))

  checkAll("Int to Boolean", PrismLaws(intToBoolean))
  checkAll("Int to Byte"   , PrismLaws(intToByte))
  checkAll("Int to Char"   , PrismLaws(intToChar))

}
