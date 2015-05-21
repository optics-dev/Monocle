package monocle.std

import org.specs2.scalaz.{ScalazMatchers, Spec}

import monocle.std.{validation => mValidation}

import scalaz.syntax.validation._
import scalaz.syntax.either._

class ValidationExample extends Spec {
  "success defines a Prism that can get, set or modify the underlying value of a Success instance" in {
    mValidation.success.getOrModify(123.success) ==== 123.right
    mValidation.success.getOrModify("abc".failure) ==== "abc".failure.left

    mValidation.success.getOption(123.success) ==== Some(123)
    mValidation.success.getOption("abc".failure) ==== None

    mValidation.success.reverseGet(123) ==== 123.success

    mValidation.success.set('a')(123.success) ==== 'a'.success
    mValidation.success.set(123)("e".failure) ==== "e".failure

    mValidation.success[String, Int, Double].modify(_ + 2.34D)(10.success) ==== 12.34D.success
    mValidation.success[String, String, Int].modify(_.toInt)("abc".failure) ==== "abc".failure
  }

  "failure defines a Prism that can get, set or modify the underlying value of a Failure instance" in {
    mValidation.failure.getOrModify(123.success) ==== 123.success.left
    mValidation.failure.getOrModify("abc".failure) ==== "abc".right

    mValidation.failure.getOption("abc".failure) ==== Some("abc")
    mValidation.failure.getOption(123.success) ==== None

    mValidation.failure.reverseGet(123) ==== 123.failure

    mValidation.failure.set('a')(1.failure) ==== 'a'.failure
    mValidation.failure.set(2)("e".success) ==== "e".success

    mValidation.failure[Int, String, Double].modify(_ + 2.34D)(10.failure) ==== 12.34D.failure
    mValidation.failure[String, String, Int].modify(_.toInt)("abc".success) ==== "abc".success
  }
}
