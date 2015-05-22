package monocle.std

import monocle.TestUtil._
import monocle.law.{IsoLaws, PrismLaws}
import org.specs2.scalaz.Spec

class ValidationSpec extends Spec {
  checkAll("Validation is isomorphic to Disjunction", IsoLaws(monocle.std.validation.disjunctionIso[String, String, Int, Int]))
  checkAll("success", PrismLaws(monocle.std.validation.success[Int, String, String]))
  checkAll("failure", PrismLaws(monocle.std.validation.failure[Int, String, Int]))
}
