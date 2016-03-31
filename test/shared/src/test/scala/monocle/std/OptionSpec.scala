package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.{PrismTests, IsoTests}
import monocle.law.discipline.function.{EachTests, EmptyTests}

class OptionSpec extends MonocleSuite {
  checkAll("some", PrismTests(some[Int]))
  checkAll("none", PrismTests(none[Long]))
  checkAll("optionToDisjunction",  IsoTests(optionToDisjunction[Int]))
  checkAll("pOptionToDisjunction", IsoTests(pOptionToDisjunction[Int, Int]))

  checkAll("each Option", EachTests[Option[Int], Int])
  checkAll("empty Option",EmptyTests[Option[Int]])
}
