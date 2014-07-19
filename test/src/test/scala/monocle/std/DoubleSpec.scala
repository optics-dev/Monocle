package monocle.std

import monocle.PrismLaws
import monocle.TestUtil._
import monocle.function._
import org.specs2.scalaz.Spec

class DoubleSpec extends Spec {

  checkAll("safeCast Double to Int", PrismLaws(safeCast[Double,Int]))

}
