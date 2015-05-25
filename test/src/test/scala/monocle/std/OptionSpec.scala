package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests
import monocle.law.discipline.function.{EachTests, EmptyTests}

class OptionSpec extends MonocleSuite {

  checkAll("some", PrismTests(monocle.std.some[Int, Int]))
  checkAll("none", PrismTests(monocle.std.none[Long]))

  checkAll("each Option", EachTests[Option[Int], Int])
  checkAll("empty Option",EmptyTests[Option[Int]])

}
