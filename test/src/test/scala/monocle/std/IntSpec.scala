package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests
import monocle.law.discipline.function.IndexTests

class IntSpec extends MonocleSuite {

  checkAll("Int index bit", IndexTests.defaultIntIndex[Int, Boolean])

  checkAll("Int to Boolean", PrismTests(intToBoolean))
  checkAll("Int to Byte"   , PrismTests(intToByte))
  checkAll("Int to Char"   , PrismTests(intToChar))

}
