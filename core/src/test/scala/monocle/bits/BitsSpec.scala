package monocle.bits

import monocle.Lens
import org.specs2.scalaz.Spec
import scalaz.std.AllInstances._


class BitsSpec extends Spec {

  val atIntFirst = atBit[Int](0)
  val atIntLast = atBit[Int](-1)

  val atCharFirst = atBit[Char](0)
  val atCharLast = atBit[Char](-1)

  checkAll(Lens.laws(atIntFirst))
  checkAll(Lens.laws(atIntLast))

  checkAll(Lens.laws(atCharFirst))
  checkAll(Lens.laws(atCharLast))
}
