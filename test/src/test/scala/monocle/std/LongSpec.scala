package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{PrismLaws, LensLaws}
import org.specs2.scalaz.Spec

class LongSpec extends Spec {

  checkAll("atBit Long", LensLaws(atBit[Long](0)))

  checkAll("safeCast Long to Int"     , PrismLaws(safeCast[Long, Int]))
  checkAll("safeCast Long to Char"    , PrismLaws(safeCast[Long, Char]))
  checkAll("safeCast Long to Byte"    , PrismLaws(safeCast[Long, Byte]))
  checkAll("safeCast Long to Boolean ", PrismLaws(safeCast[Long, Boolean]))

}
