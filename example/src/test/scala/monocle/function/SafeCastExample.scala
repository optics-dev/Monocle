package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.Maybe

class SafeCastExample extends Spec {

  "safeCast creates a Prism from Int to Char" in {
    safeCast[Int, Char].getMaybe(65)    ==== Maybe.just('A')
    safeCast[Int, Char].reverseGet('a') ==== 97

    // with Maybe.just syntax sugar
    (65 applyPrism safeCast[Int, Char] getMaybe) ==== Maybe.just('A')
  }

  "safeCast creates a Prism from Double to Int" in {
    safeCast[Double, Int].getMaybe(5d) ==== Maybe.just(5)

    safeCast[Double, Int].getMaybe(5.4d)                    ==== Maybe.empty
    safeCast[Double, Int].getMaybe(Double.PositiveInfinity) ==== Maybe.empty
    safeCast[Double, Int].getMaybe(Double.NaN)              ==== Maybe.empty
  }

  "safeCast creates a Prism from String to Int" in {
    safeCast[String, Int].getMaybe("352")  ==== Maybe.just(352)
    safeCast[String, Int].getMaybe("-352") ==== Maybe.just(-352)
    safeCast[String, Int].getMaybe("୨")    ==== Maybe.empty // Non ascii digits
    safeCast[String, Int].getMaybe("")     ==== Maybe.empty
    // we reject case where String starts with +, otherwise it will be an invalid Prism according 2nd Prism law
    safeCast[String, Int].getMaybe("+352") ==== Maybe.empty

    safeCast[String, Int].reverseGet(8921)  ==== "8921"
    safeCast[String, Int].reverseGet(-32)   ==== "-32"

    safeCast[String, Int].modify("1024", _ * 2) ==== "2048"
  }

  "safeCast creates a Prism from String to Boolean" in {
    safeCast[String, Boolean].getMaybe("true") ==== Maybe.just(true)
    safeCast[String, Boolean].reverseGet(false) ==== "false"
  }

 // idem for all other instances of SafeCast

}
