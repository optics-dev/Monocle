package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

class CharSpec extends MonocleSuite {
  checkAll("Char to Boolean ", PrismTests(charToBoolean))
}
