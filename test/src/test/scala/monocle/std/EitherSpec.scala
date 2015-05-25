package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

class EitherSpec extends MonocleSuite {

  checkAll("std left" , PrismTests(stdLeft[Int, String, Int]))
  checkAll("std right", PrismTests(stdRight[Int, String, String]))

}
