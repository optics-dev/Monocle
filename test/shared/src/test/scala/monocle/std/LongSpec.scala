package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

class LongSpec extends MonocleSuite {
  checkAll("Long to Int", PrismTests(longToInt))
  checkAll("Long to Char", PrismTests(longToChar))
  checkAll("Long to Byte", PrismTests(longToByte))
  checkAll("Long to Boolean", PrismTests(longToBoolean))
}
