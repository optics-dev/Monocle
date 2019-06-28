package monocle.std

import monocle.MonocleSuite
import monocle.std.{validated => mValidated}

import cats.syntax.validated._

class ValidatedExample extends MonocleSuite {
  test("success defines a Prism that can get or set the underlying value of a Success instance") {
    mValidated.success.getOption(123.valid)     shouldEqual Some(123)
    mValidated.success.getOption("abc".invalid) shouldEqual None

    mValidated.success.set(555)(123.valid)     shouldEqual 555.valid
    mValidated.success.set(123)("abc".invalid) shouldEqual "abc".invalid
  }

  test("failure defines a Prism that can modify the underlying value of a Failure instance") {
    mValidated.failure[String, Int].modify(_.reverse)("abc".invalid) shouldEqual "cba".invalid
  }
}
