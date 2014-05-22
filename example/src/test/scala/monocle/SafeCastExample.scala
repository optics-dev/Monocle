package monocle

import monocle.function.SafeCast._
import monocle.syntax._
import org.specs2.scalaz.Spec

class SafeCastExample extends Spec {

  "safeCast creates a Prism from Int to Char" in {
    safeCast[Int, Char].getOption(65)   shouldEqual Some('A')
    safeCast[Int, Char].reverseGet('a') shouldEqual 97

    safeCast[Int, Char].reverseModify('b', _ - 1) shouldEqual Some('a')
    safeCast[Int, Char].reverseModify('b', _ + 1) shouldEqual Some('c')
    safeCast[Int, Char].reverseModify('b', _ + Char.MaxValue.toInt) shouldEqual None

    // with some syntax sugar
    (65 <-? safeCast[Int, Char] getOption) shouldEqual Some('A')
  }

  "safeCast creates a Prism from String to Int" in {
    safeCast[String, Int].getOption("352") shouldEqual Some(352)
    safeCast[String, Int].reverseGet(8921) shouldEqual "8921"
    safeCast[String, Int].getOption("")    shouldEqual None
  }

  "With safeCast[String,Int] one could map a String with an Int => Int function." in {
    safeCast[String, Int].modify("1024", _ * 2) shouldEqual "2048"
  }

  "safeCast creates a Prism from String to Boolean" in {
    safeCast[String, Boolean].getOption("true") shouldEqual Some(true)
    safeCast[String, Boolean].reverseGet(false) shouldEqual "false"
  }

 // idem for all other instances of SafeCast

}
