package monocle.function

import monocle.PrismLaws
import monocle.TestUtil._
import monocle.function.SafeCast._
import org.specs2.scalaz.Spec

class SafeCastSpec extends Spec {

  checkAll("Byte to Boolean safe cast", PrismLaws(safeCast[Byte,Boolean]))

  checkAll("Char to Boolean safe cast", PrismLaws(safeCast[Char,Boolean]))

  checkAll("Int to Boolean safe cast", PrismLaws(safeCast[Int,Boolean]))
  checkAll("Int to Byte safe cast"   , PrismLaws(safeCast[Int,Byte]))
  checkAll("Int to Char safe cast"   , PrismLaws(safeCast[Int,Char]))

  checkAll("Long to Boolean safe cast", PrismLaws(safeCast[Long,Boolean]))
  checkAll("Long to Byte safe cast"   , PrismLaws(safeCast[Long,Byte]))
  checkAll("Long to Char safe cast"   , PrismLaws(safeCast[Long,Char]))

  checkAll("Double to Int safe cast"  , PrismLaws(safeCast[Double,Int]))

  checkAll("String to Boolean safe cast", PrismLaws(safeCast[String,Boolean]))
  checkAll("String to Byte safe cast"   , PrismLaws(safeCast[String,Byte]))
  checkAll("String to Int safe cast"    , PrismLaws(safeCast[String,Int]))
  checkAll("String to Long safe cast"   , PrismLaws(safeCast[String,Long]))

}
