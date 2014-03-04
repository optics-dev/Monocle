package monocle.bits

import monocle.Lens
import org.specs2.scalaz.Spec
import scalaz.std.AllInstances._
import scalaz.Equal


class BitsSpec extends Spec {
  import monocle.std.char._
  import monocle.std.int._

  implicit val intEqual  = Equal.equalA[Int]
  implicit val charEqual = Equal.equalA[Char]

  checkAll(Lens.laws(atBit[Int](0)))
  checkAll(Lens.laws(atBit[Int](-1)))

  checkAll(Lens.laws(atBit[Char](0)))
}
