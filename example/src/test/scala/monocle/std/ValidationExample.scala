package monocle.std

import monocle.MonocleSuite
import monocle.std.{validation => mValidation}

import scalaz.syntax.validation._

class ValidationExample extends MonocleSuite {
  test("success defines a Prism that can get or set the underlying value of a Success instance") {
    mValidation.success.getOption(123.success) shouldEqual Some(123)
    mValidation.success.getOption("abc".failure) shouldEqual None

    mValidation.success.set('a')(123.success) shouldEqual 'a'.success
    mValidation.success.set(123)("abc".failure) shouldEqual "abc".failure
  }

  test("failure defines a Prism that can modify the underlying value of a Failure instance") {
    mValidation.failure[Int, String, Double].modify(_ + 2.34D)(10.failure) shouldEqual 12.34D.failure
    mValidation.failure[String, String, Int].modify(_.toInt)("abc".success) shouldEqual "abc".success
  }
}
