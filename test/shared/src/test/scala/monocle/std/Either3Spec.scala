package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

class Either3Spec extends MonocleSuite {
  checkAll("either3 left3"  , PrismTests(left3[String, Int, Char]))
  checkAll("either3 middle3", PrismTests(middle3[String, Int, Char]))
  checkAll("either3 right3" , PrismTests(right3[String, Int, Char]))
}
