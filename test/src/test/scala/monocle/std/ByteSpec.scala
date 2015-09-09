package monocle.std

import monocle.MonocleSuite
import monocle.function.all._
import monocle.std.byte._
import monocle.law.discipline.{OptionalTests, PrismTests}

class ByteSpec extends MonocleSuite {
  checkAll("Byte index bit", OptionalTests(index[Byte, Int, Boolean](0)))
  checkAll("Byte to Boolean", PrismTests(byteToBoolean))
}
