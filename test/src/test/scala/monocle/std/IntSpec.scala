package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

class IntSpec extends MonocleSuite {
  checkAll("Int to Boolean", PrismTests(intToBoolean))
  checkAll("Int to Byte"   , PrismTests(intToByte))
  checkAll("Int to Char"   , PrismTests(intToChar))
}
