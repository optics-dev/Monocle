package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.{PrismTests, IsoTests}
import monocle.law.discipline.function.{EachTests, EmptyTests}

class OptionSpec extends MonocleSuite {

  checkAll("some", PrismTests(monocle.std.some[Int]))
  checkAll("none", PrismTests(monocle.std.none[Long]))

  checkAll("each Option", EachTests[Option[Int], Int])
  checkAll("empty Option",EmptyTests[Option[Int]])

  checkAll("optionToDisjunction",  IsoTests(monocle.std.option.optionToDisjunction[Int]))
  checkAll("pOptionToDisjunction", IsoTests(monocle.std.option.pOptionToDisjunction[Int, Int]))
}
