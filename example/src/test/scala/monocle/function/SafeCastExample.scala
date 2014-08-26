package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.Maybe

class SafeCastExample extends Spec {

  "safeCast creates a Prism from Int to Char" in {
    safeCast[Int, Char].getMaybe(65)    shouldEqual Maybe.just('A')
    safeCast[Int, Char].reverseGet('a') shouldEqual 97

    // with Maybe.just syntax sugar
    (65 applyPrism safeCast[Int, Char] getMaybe) shouldEqual Maybe.just('A')
  }

  "safeCast creates a Prism from Double to Int" in {
    safeCast[Double, Int].getMaybe(5d) shouldEqual Maybe.just(5)

    safeCast[Double, Int].getMaybe(5.4d)                    shouldEqual Maybe.empty
    safeCast[Double, Int].getMaybe(Double.PositiveInfinity) shouldEqual Maybe.empty
    safeCast[Double, Int].getMaybe(Double.NaN)              shouldEqual Maybe.empty
  }

  "safeCast creates a Prism from String to Int" in {
    safeCast[String, Int].getMaybe("352")  shouldEqual Maybe.just(352)
    safeCast[String, Int].getMaybe("-352") shouldEqual Maybe.just(-352)
    safeCast[String, Int].getMaybe("୨")    shouldEqual Maybe.empty // Non ascii digits
    safeCast[String, Int].getMaybe("")     shouldEqual Maybe.empty
    // we reject case where String starts with +, otherwise it will be an invalid Prism according 2nd Prism law
    safeCast[String, Int].getMaybe("+352") shouldEqual Maybe.empty

    safeCast[String, Int].reverseGet(8921)  shouldEqual "8921"
    safeCast[String, Int].reverseGet(-32)   shouldEqual "-32"

    safeCast[String, Int].modify("1024", _ * 2) shouldEqual "2048"
  }

  "safeCast creates a Prism from String to Boolean" in {
    safeCast[String, Boolean].getMaybe("true") shouldEqual Maybe.just(true)
    safeCast[String, Boolean].reverseGet(false) shouldEqual "false"
  }

 // idem for all other instances of SafeCast

}
