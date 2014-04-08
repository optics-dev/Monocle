package monocle.std

import monocle.Iso
import monocle.TestUtil._
import monocle.std.string._
import org.specs2.scalaz.Spec

class StringSpec extends Spec {

  checkAll("stringToList", Iso.laws(stringToList))

}
