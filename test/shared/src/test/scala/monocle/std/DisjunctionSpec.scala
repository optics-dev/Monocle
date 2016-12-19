package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.{IsoTests, PrismTests}
import monocle.law.discipline.function.PossibleTests
import scalaz.\/

class DisjunctionSpec extends MonocleSuite {
  checkAll("disjunction left" , PrismTests(left[String, Int]))
  checkAll("disjunction right", PrismTests(right[String, Int]))

  checkAll("disjunction to Validation", IsoTests(disjunctionToValidation[String, Int]))
  checkAll("disjunction to Either"    , IsoTests(disjunctionToEither[String, Int]))

  checkAll("possible disjunction", PossibleTests[Unit \/ Int, Int])
}
