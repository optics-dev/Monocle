package monocle.std

import org.specs2.scalaz.Spec
import monocle.Prism
import monocle.std.int._
import monocle.TestUtil._


class IntSpec extends Spec {
  checkAll("longToInt", Prism.laws(longToInt))
  checkAll("stringToInt", Prism.laws(stringToInt))
}
