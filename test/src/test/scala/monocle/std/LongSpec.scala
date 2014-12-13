package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.{OptionalLaws, PrismLaws}
import org.specs2.scalaz.Spec

class LongSpec extends Spec {

  checkAll("Long index bit", OptionalLaws(index[Long, Int, Boolean](0)))

  checkAll("Long to Int"    , PrismLaws(longToInt))
  checkAll("Long to Char"   , PrismLaws(longToChar))
  checkAll("Long to Byte"   , PrismLaws(longToByte))
  checkAll("Long to Boolean", PrismLaws(longToBoolean))

}
