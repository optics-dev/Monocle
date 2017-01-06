package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

class PlatformSpecificStringsSpec extends MonocleSuite {
  checkAll("String to URL", PrismTests(stringToURL))
}
