package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{PrismLaws, OptionalLaws, TraversalLaws, IsoLaws}
import org.specs2.scalaz.Spec

class StringSpec extends Spec {

  checkAll("stringToList", IsoLaws(stringToList))


  checkAll("cons - snoc String", ConsSnocLaws[String, Char])

  checkAll("each String", TraversalLaws(each[String, Char]))

  checkAll("filterIndex String", TraversalLaws(filterIndex[String, Int, Char](_ % 2 == 0)))

  checkAll("headOption String", OptionalLaws(headOption[String, Char]))

  checkAll("index String", OptionalLaws(index[String, Int, Char](2)))

  checkAll("initOption String", OptionalLaws(initOption[String, String]))

  checkAll("lastOption String", OptionalLaws(lastOption[String, Char]))

  checkAll("reverse String", IsoLaws(_reverse[String, String]))

  checkAll("safeCast String to Boolean ", PrismLaws(safeCast[String,Boolean]))
  checkAll("safeCast String to Byte"    , PrismLaws(safeCast[String,Byte]))
  checkAll("safeCast String to Int"     , PrismLaws(safeCast[String,Int]))
  checkAll("safeCast String to Long"    , PrismLaws(safeCast[String,Long]))

  checkAll("snoc String", PrismLaws(_cons[String, Char]))

  checkAll("tailOption String", OptionalLaws(tailOption[String, String]))

}
