package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

class DoubleSpec extends MonocleSuite {
  checkAll("Double to Int"  , PrismTests(doubleToInt))
  checkAll("Double to Float", PrismTests(doubleToFloat))
}
