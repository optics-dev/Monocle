package monocle.bits

import monocle.Lens
import org.specs2.scalaz.Spec
import scalaz.std.AllInstances._
import scalaz.Equal

class BitsSpec extends Spec {
  import monocle.std.char._
  import monocle.std.int._
  import monocle.std.byte._
  import monocle.std.boolean._

  implicit val intEqual = Equal.equalA[Int]
  implicit val charEqual = Equal.equalA[Char]

  implicit val byteEqual = Equal.equalA[Byte]
  implicit val booleanEqual = Equal.equalA[Boolean]

  checkAll(Lens.laws(atBit[Int](0)))
  checkAll(Lens.laws(atBit[Int](-1)))

  checkAll(Lens.laws(atBit[Char](0)))

  checkAll(Lens.laws(atBit[Byte](0)))
  checkAll(Lens.laws(atBit[Byte](-1)))

  checkAll(Lens.laws(atBit[Boolean](0)))
  checkAll(Lens.laws(atBit[Boolean](-1)))

}
