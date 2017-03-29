package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

class BigIntSpec extends MonocleSuite {
  checkAll("BigInt to Long"   , PrismTests(bigIntToLong))
  checkAll("BigInt to Int"    , PrismTests(bigIntToInt))
  checkAll("BigInt to Char"   , PrismTests(bigIntToChar))
  checkAll("BigInt to Byte"   , PrismTests(bigIntToByte))
  checkAll("BigInt to Boolean", PrismTests(bigIntToBoolean))
}
