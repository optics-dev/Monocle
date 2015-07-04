package monocle.std

import monocle.MonocleSuite
import monocle.std.{validation => mValidation}

import scalaz.syntax.validation._

class ValidationExample extends MonocleSuite {
  test("success defines a Prism that can get or set the underlying value of a Success instance") {
    mValidation.success.getOption(123.success)   shouldEqual Some(123)
    mValidation.success.getOption("abc".failure) shouldEqual None

    mValidation.success.set(555)(123.success)   shouldEqual 555.success
    mValidation.success.set(123)("abc".failure) shouldEqual "abc".failure
  }

  test("failure defines a Prism that can modify the underlying value of a Failure instance") {
    mValidation.failure[String, Int].modify(_.reverse)("abc".failure) shouldEqual "cba".failure
  }
}
