package monocle.std

import monocle.PrismLaws
import monocle.TestUtil._
import monocle.std.either._
import org.specs2.scalaz.Spec

class EitherSpec extends Spec {

  checkAll("std left" , PrismLaws(left[Int, String, Int]))
  checkAll("std right", PrismLaws(right[Int, String, String]))

}
