package monocle.std

import monocle.MonocleSuite
import monocle.std.{validation => mValidation}

import cats.syntax.validated._

class ValidationExample extends MonocleSuite {
  test("success defines a Prism that can get or set the underlying value of a Success instance") {
    mValidation.success.getOption(123.valid)     shouldEqual Some(123)
    mValidation.success.getOption("abc".invalid) shouldEqual None

    mValidation.success.set(555)(123.valid)     shouldEqual 555.valid
    mValidation.success.set(123)("abc".invalid) shouldEqual "abc".invalid
  }

  test("failure defines a Prism that can modify the underlying value of a Failure instance") {
    mValidation.failure[String, Int].modify(_.reverse)("abc".invalid) shouldEqual "cba".invalid
  }
}
