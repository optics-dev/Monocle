package monocle.function

import monocle.LensLaws
import monocle.TestUtil._
import monocle.function.AtBit._
import org.specs2.scalaz.Spec

class AtBitSpec extends Spec {

  checkAll("atBit[Int] first bit"    , LensLaws(atBit[Int](0)))
  checkAll("atBit[Int] last bit"     , LensLaws(atBit[Int](-1)))

  checkAll("atBit[Char] first bit"   , LensLaws(atBit[Char](0)))
  checkAll("atBit[Byte] first bit"   , LensLaws(atBit[Byte](0)))
  checkAll("atBit[Boolean] first bit", LensLaws(atBit[Boolean](0)))
  checkAll("atBit[Long] first bit"   , LensLaws(atBit[Long](0)))

}
