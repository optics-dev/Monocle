package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.{IsoTests, PrismTests}
import monocle.law.discipline.function.PossibleTests
import scalaz.Validation

class ValidationSpec extends MonocleSuite {
  checkAll("Validation is isomorphic to Disjunction", IsoTests(monocle.std.validation.validationToDisjunction[String, Int]))
  checkAll("success", PrismTests(monocle.std.validation.success[String, Int]))
  checkAll("failure", PrismTests(monocle.std.validation.failure[String, Int]))
  checkAll("possible Validation", PossibleTests[Validation[Unit, Int], Int])
}
