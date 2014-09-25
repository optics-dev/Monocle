package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.function.SequenceLaws
import monocle.law.{IsoLaws, PrismLaws}
import org.specs2.scalaz.Spec

class StringSpec extends Spec {

  checkAll("stringToList", IsoLaws(stringToList))


  checkAll("sequence String", SequenceLaws[String, Char])

  checkAll("safeCast String to Boolean ", PrismLaws(safeCast[String,Boolean]))
  checkAll("safeCast String to Byte"    , PrismLaws(safeCast[String,Byte]))
  checkAll("safeCast String to Int"     , PrismLaws(safeCast[String,Int]))
  checkAll("safeCast String to Long"    , PrismLaws(safeCast[String,Long]))

}
