package monocle.std

import monocle.Iso
import monocle.TestUtil._
import monocle.std.string._
import org.specs2.scalaz.Spec

class StringSpec extends Spec {

  checkAll("stringToList", Iso.laws(stringToList))

  "parseLong should return Some(long) for a positive int string." in {
    parseLong("143") shouldEqual Some(143)
    parseLong("+512") shouldEqual Some(512)
  }

  "parseLong should return Some(long) for a negative int string." in {
    parseLong("-376") shouldEqual Some(-376)
  }

  "parseLong should return None for non digit strings" in {
    parseLong("hello") shouldEqual None
    parseLong("୨") shouldEqual None  // Non ascii digits
    parseLong("８") shouldEqual None // Non ascii digits
  }

  "parseLong should return None for an empty string" in {
    parseLong("") shouldEqual None
  }

  "charToDigit should return Some(digit) only for ascii [0..9] digits" in {
    charToDigit('5') shouldEqual Some(5)
    charToDigit('８') shouldEqual None // Non ascii digit
  }
}
