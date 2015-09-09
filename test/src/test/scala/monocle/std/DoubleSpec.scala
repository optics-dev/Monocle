package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

class DoubleSpec extends MonocleSuite {
  checkAll("Double to Int", PrismTests(doubleToInt))
}
