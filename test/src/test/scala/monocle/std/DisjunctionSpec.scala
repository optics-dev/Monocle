package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

class DisjunctionSpec extends MonocleSuite {

  checkAll("disjunction left" , PrismTests(monocle.std.left[String, Int]))
  checkAll("disjunction right", PrismTests(monocle.std.right[String, Int]))

}
