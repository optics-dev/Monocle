package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests
import monocle.law.discipline.function.{EachTests, PossibleTests}

class EitherSpec extends MonocleSuite {
  checkAll("either left" , PrismTests(stdLeft[String, Int]))
  checkAll("either right", PrismTests(stdRight[String, String]))
  checkAll("each Either", EachTests[Either[Unit, Int], Int])
  checkAll("possible Either", PossibleTests[Either[Unit, Int], Int])
}
