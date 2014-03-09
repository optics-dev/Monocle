package monocle.util

import monocle.Lens
import monocle.TestUtil._
import monocle.util.Bits._
import org.specs2.scalaz.Spec

class BitsSpec extends Spec {

  import monocle.std.boolean._
  import monocle.std.byte._
  import monocle.std.char._
  import monocle.std.int._
  import monocle.std.long._

  checkAll("atBit[Int] first bit"    , Lens.laws(atBit[Int](0)))
  checkAll("atBit[Int] last bit"     , Lens.laws(atBit[Int](-1)))

  checkAll("atBit[Char] first bit"   , Lens.laws(atBit[Char](0)))

  checkAll("atBit[Byte] first bit"   , Lens.laws(atBit[Byte](0)))

  checkAll("atBit[Boolean] first bit", Lens.laws(atBit[Boolean](0)))

  checkAll("atBit[Long] first bit"   , Lens.laws(atBit[Long](0)))

}
