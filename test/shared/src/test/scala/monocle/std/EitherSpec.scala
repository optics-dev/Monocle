package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

class EitherSpec extends MonocleSuite {
  checkAll("either left" , PrismTests(stdLeft[String, Int]))
  checkAll("either right", PrismTests(stdRight[String, String]))
}
