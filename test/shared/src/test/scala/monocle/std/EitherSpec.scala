package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests
import monocle.law.discipline.function.PossibleTests

class EitherSpec extends MonocleSuite {
  checkAll("either left" , PrismTests(stdLeft[String, Int]))
  checkAll("either right", PrismTests(stdRight[String, String]))
  checkAll("possible Either", PossibleTests[Either[Unit, Int], Int])
}
