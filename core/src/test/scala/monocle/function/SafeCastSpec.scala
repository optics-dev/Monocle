package monocle.function

import monocle.Prism
import monocle.TestUtil._
import monocle.function.SafeCast._
import org.specs2.scalaz.Spec

class SafeCastSpec extends Spec {

  checkAll("Byte to Boolean safe cast", Prism.laws(safeCast[Byte,Boolean]))

  checkAll("Char to Boolean safe cast", Prism.laws(safeCast[Char,Boolean]))

  checkAll("Int to Boolean safe cast", Prism.laws(safeCast[Int,Boolean]))
  checkAll("Int to Byte safe cast"   , Prism.laws(safeCast[Int,Byte]))
  checkAll("Int to Char safe cast"   , Prism.laws(safeCast[Int,Char]))

  checkAll("Long to Boolean safe cast", Prism.laws(safeCast[Long,Boolean]))
  checkAll("Long to Byte safe cast"   , Prism.laws(safeCast[Long,Byte]))
  checkAll("Long to Char safe cast"   , Prism.laws(safeCast[Long,Char]))

  checkAll("String to Boolean safe cast", Prism.laws(safeCast[String,Boolean]))
  checkAll("String to Byte safe cast"   , Prism.laws(safeCast[String,Byte]))
  checkAll("String to Int safe cast"    , Prism.laws(safeCast[String,Int]))
  checkAll("String to Long safe cast"   , Prism.laws(safeCast[String,Long]))

}
