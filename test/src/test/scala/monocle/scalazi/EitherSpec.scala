package monocle.scalazi

import monocle.PrismLaws
import monocle.TestUtil._
import org.specs2.scalaz.Spec

class EitherSpec extends Spec {

  checkAll("scalaz left" , PrismLaws( left[Int, String, Int]))
  checkAll("scalaz right", PrismLaws(right[Int, String, String]))

}
