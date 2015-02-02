package monocle.std

import monocle.TestUtil._
import monocle.law.{PrismLaws}
import org.specs2.scalaz.Spec

class ValidationSpec extends Spec {

  checkAll("success", PrismLaws(validation.success[String, Int]))
  checkAll("failure", PrismLaws(validation.failure[String, Int]))

}
