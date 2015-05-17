package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

class TheseSpec extends MonocleSuite {
  checkAll("These - Disjunction" , PrismTests(theseDisjunction[Int, String]))
}
