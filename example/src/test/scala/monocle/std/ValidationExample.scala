package monocle.std

import org.specs2.scalaz.{ScalazMatchers, Spec}

import monocle.std.{validation => mValidation}

import scalaz.syntax.validation._
import scalaz.syntax.either._

class ValidationExample extends Spec {
  "success defines a Prism that can get or set the underlying value of a Success instance" in {
    mValidation.success.getOption(123.success) ==== Some(123)
    mValidation.success.getOption("abc".failure) ==== None

    mValidation.success.set('a')(123.success) ==== 'a'.success
    mValidation.success.set(123)("abc".failure) ==== "abc".failure
  }

  "failure defines a Prism that can modify the underlying value of a Failure instance" in {
    mValidation.failure[Int, String, Double].modify(_ + 2.34D)(10.failure) ==== 12.34D.failure
    mValidation.failure[String, String, Int].modify(_.toInt)("abc".success) ==== "abc".success
  }
}
