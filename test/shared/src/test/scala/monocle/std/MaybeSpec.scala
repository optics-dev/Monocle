package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function.{EachTests, PossibleTests, EmptyTests}
import monocle.law.discipline.{IsoTests, PrismTests}

import scalaz.Maybe

class MaybeSpec extends MonocleSuite {
  checkAll("maybeToOption", IsoTests(maybeToOption[Int]))
  checkAll("just"   , PrismTests(just[Int]))
  checkAll("nothing", PrismTests(nothing[Long]))

  checkAll("each Maybe", EachTests[Maybe[Int], Int])
  checkAll("empty Maybe", EmptyTests[Maybe[Int]])
  checkAll("possible Maybe", PossibleTests[Maybe[Int], Int])
}
