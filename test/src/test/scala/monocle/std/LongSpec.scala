package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests
import monocle.law.discipline.function.IndexTests

class LongSpec extends MonocleSuite {
  checkAll("Long index bit", IndexTests.defaultIntIndex[Long, Boolean])

  checkAll("Long to Int"    , PrismTests(longToInt))
  checkAll("Long to Char"   , PrismTests(longToChar))
  checkAll("Long to Byte"   , PrismTests(longToByte))
  checkAll("Long to Boolean", PrismTests(longToBoolean))
}
