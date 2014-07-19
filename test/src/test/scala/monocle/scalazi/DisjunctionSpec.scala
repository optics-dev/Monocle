package monocle.scalazi

import monocle.PrismLaws
import monocle.TestUtil._
import org.specs2.scalaz.Spec

class DisjunctionSpec extends Spec {

  checkAll("disjunction left" , PrismLaws( left[Int, String, Int]))
  checkAll("disjunction right", PrismLaws(right[Int, String, String]))

}
