package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.OptionalTests

class BooleanSpec extends MonocleSuite {
  checkAll("Boolean index bit", OptionalTests(index[Boolean, Int, Boolean](0)))
}
