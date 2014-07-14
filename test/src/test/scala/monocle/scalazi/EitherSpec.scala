package monocle.scalazi

import monocle.PrismLaws
import org.specs2.scalaz.Spec
import monocle.TestUtil._

class EitherSpec extends Spec {

  checkAll("scalaz left" , PrismLaws( left[Int, String, Int]))
  checkAll("scalaz right", PrismLaws(right[Int, String, String]))

}
