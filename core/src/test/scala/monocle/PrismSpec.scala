package monocle

import monocle.TestUtil._
import monocle.std.char._
import org.specs2.scalaz.Spec

class PrismSpec extends Spec {

  checkAll(Prism.laws(intToChar))

}
