package monocle.bits

import monocle.Lens
import org.specs2.scalaz.Spec
import scalaz.std.AllInstances._


class BitsSpec extends Spec {

  val atFirst = atBit[Int](0)
  val atLast = atBit[Int](-1)

  checkAll(Lens.laws(atFirst))
  checkAll(Lens.laws(atLast))
}
