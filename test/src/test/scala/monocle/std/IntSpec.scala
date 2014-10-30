package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.{PrismLaws, LensLaws}
import org.specs2.scalaz.Spec

class IntSpec extends Spec {

  checkAll("atBit Int", LensLaws(atBit[Int](0)))

  checkAll("safeCast Int to Boolean ", PrismLaws(safeCast[Int,Boolean]))
  checkAll("safeCast Int to Byte"    , PrismLaws(safeCast[Int,Byte]))
  checkAll("safeCast Int to Char"    , PrismLaws(safeCast[Int,Char]))

}
