package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.{IsoTests, PrismTests}
import monocle.law.discipline.function.{EachTests, PossibleTests}
import cats.data.Validated

class ValidatedSpec extends MonocleSuite {
  import cats.laws.discipline.arbitrary._

  checkAll("Validated is isomorphic to Disjunction", IsoTests(monocle.std.validation.validationToDisjunction[String, Int]))
  checkAll("success", PrismTests(monocle.std.validation.success[String, Int]))
  checkAll("failure", PrismTests(monocle.std.validation.failure[String, Int]))
  checkAll("each Validated", EachTests[Validated[Unit, Int], Int])
  checkAll("possible Validated", PossibleTests[Validated[Unit, Int], Int])
}
