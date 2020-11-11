package monocle.std

import monocle.MonocleSuite
import monocle.std.{validated => mValidated}

import cats.syntax.validated._

class ValidatedExample extends MonocleSuite {
  test("success defines a Prism that can get or set the underlying value of a Success instance") {
    assertEquals(mValidated.success.getOption(123.valid), Some(123))
    assertEquals(mValidated.success.getOption("abc".invalid), None)

    assertEquals(mValidated.success.set(555)(123.valid), 555.valid)
    assertEquals(mValidated.success.set(123)("abc".invalid), "abc".invalid)
  }

  test("failure defines a Prism that can modify the underlying value of a Failure instance") {
    assertEquals(mValidated.failure[String, Int].modify(_.reverse)("abc".invalid), "cba".invalid)
  }
}
