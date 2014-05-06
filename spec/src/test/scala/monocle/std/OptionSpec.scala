package monocle.std

import monocle.PrismLaws
import monocle.TestUtil._
import monocle.std.option._
import org.specs2.scalaz.Spec

class OptionSpec extends Spec {

  checkAll("some", PrismLaws(some[Int, Int]))
  checkAll("none", PrismLaws(none[Long]))

}
