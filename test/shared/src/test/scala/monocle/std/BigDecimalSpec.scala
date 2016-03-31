package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

class BigDecimalSpec extends MonocleSuite {
  checkAll("BigDecimal to Long"  , PrismTests(bigDecimalToLong))
  checkAll("BigDecimal to Int"   , PrismTests(bigDecimalToInt))
}
