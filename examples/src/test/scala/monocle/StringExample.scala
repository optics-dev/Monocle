package monocle

import monocle.std.string._
import org.specs2.scalaz.Spec

class StringExample extends Spec {
  "stringToInt is a prism from String to Int" in {
    stringToInt.getOption("352") shouldEqual Some(352)
    stringToInt.reverseGet(8921) shouldEqual "8921"
  }

  "stringToBoolean is a prism from String to Boolean" in {
    stringToBoolean.getOption("true") shouldEqual Some(true)
    stringToBoolean.reverseGet(false) shouldEqual "false"
  }
}
