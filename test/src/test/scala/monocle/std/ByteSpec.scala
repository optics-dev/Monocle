package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

class ByteSpec extends MonocleSuite {
  checkAll("Byte to Boolean", PrismTests(byteToBoolean))
}
