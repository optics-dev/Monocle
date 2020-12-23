package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.{IsoTests, PrismTests}
import monocle.law.discipline.function.{EachTests, PossibleTests}
import cats.data.Validated

import scala.annotation.nowarn

class ValidatedSpec extends MonocleSuite {
  import cats.laws.discipline.arbitrary._

  checkAll(
    "Validated is isomorphic to Disjunction",
    IsoTests(monocle.std.validated.validationToDisjunction[String, Int])
  )
  checkAll("success", PrismTests(monocle.std.validated.success[String, Int]))
  checkAll("failure", PrismTests(monocle.std.validated.failure[String, Int]))
  checkAll("each Validated", EachTests[Validated[Unit, Int], Int])
  checkAll("possible Validated", PossibleTests[Validated[Unit, Int], Int]): @nowarn
}
