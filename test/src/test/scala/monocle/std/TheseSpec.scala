package monocle.std

import monocle.TestUtil._
import monocle.law.PrismLaws
import org.specs2.scalaz.Spec

class TheseSpec extends Spec {
  checkAll("These - Disjunction" , PrismLaws( theseDisjunction[Int, String]))
}
