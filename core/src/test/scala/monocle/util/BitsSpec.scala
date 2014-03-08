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

  checkAll(Lens.laws(atBit[Int](0)))
  checkAll(Lens.laws(atBit[Int](-1)))

  checkAll(Lens.laws(atBit[Char](0)))

  checkAll(Lens.laws(atBit[Byte](0)))

  checkAll(Lens.laws(atBit[Boolean](0)))

}
