package monocle.std

import monocle.Prism
import monocle.TestUtil._
import monocle.std.byte._
import org.specs2.scalaz.Spec


class ByteSpec extends Spec {

  checkAll(Prism.laws(intToByte))
  checkAll(Prism.laws(longToByte))

}
