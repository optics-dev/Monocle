package monocle.std

import monocle.TestUtil._
import monocle.law.PrismLaws
import org.specs2.scalaz.Spec

class DisjunctionSpec extends Spec {

  checkAll("disjunction left" , PrismLaws( left[Int, String, Int]))
  checkAll("disjunction right", PrismLaws(right[Int, String, String]))

}
