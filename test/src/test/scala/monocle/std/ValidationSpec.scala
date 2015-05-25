package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.{IsoTests, PrismTests}

class ValidationSpec extends MonocleSuite {
  checkAll("Validation is isomorphic to Disjunction", IsoTests(monocle.std.validation.disjunctionIso[String, String, Int, Int]))
  checkAll("success", PrismTests(monocle.std.validation.success[Int, String, String]))
  checkAll("failure", PrismTests(monocle.std.validation.failure[Int, String, Int]))
}
