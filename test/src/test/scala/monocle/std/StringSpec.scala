package monocle.std

import monocle.TestUtil._
import monocle.law.function.SequenceLaws
import monocle.law.{IsoLaws, PrismLaws}
import org.specs2.scalaz.Spec

class StringSpec extends Spec {

  checkAll("stringToList", IsoLaws(stringToList))


  checkAll("sequence String", SequenceLaws[String, Char])

  checkAll("String to Boolean ", PrismLaws(stringToBoolean))
  checkAll("String to Byte"    , PrismLaws(stringToByte))
  checkAll("String to Int"     , PrismLaws(stringToInt))
  checkAll("String to Long"    , PrismLaws(stringToLong))

}
