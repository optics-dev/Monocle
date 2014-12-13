package monocle.std

import monocle.TestUtil._
import monocle.law.PrismLaws
import org.specs2.scalaz.Spec

class DoubleSpec extends Spec {

  checkAll("Double to Int", PrismLaws(doubleToInt))

}
