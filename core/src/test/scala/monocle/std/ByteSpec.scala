package monocle.std

import monocle.Prism
import monocle.TestUtil._
import monocle.std.byte._
import org.specs2.scalaz.Spec


class ByteSpec extends Spec {

  checkAll("intToByte", Prism.laws(intToByte))
  checkAll("longToByte", Prism.laws(longToByte))

}
