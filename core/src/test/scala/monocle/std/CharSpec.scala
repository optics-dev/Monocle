package monocle.std

import monocle.Prism
import monocle.TestUtil._
import monocle.std.char._
import org.specs2.scalaz.Spec

class CharSpec extends Spec {

  checkAll("intToChar", Prism.laws(intToChar))
  checkAll("longToChar", Prism.laws(longToChar))

}
