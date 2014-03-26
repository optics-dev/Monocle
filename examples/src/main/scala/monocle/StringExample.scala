//Created By Ilan Godik
package monocle

import monocle.std.string._

object StringExample {
  stringToInt.getOption("352") // Some(352)
  stringToInt.re.get(8921) // "8921"

  stringToBoolean.getOption("true") // Some(true)
  stringToBoolean.re.get(false) // "false"
}
