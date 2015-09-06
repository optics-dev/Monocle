package monocle.std

import monocle.MonocleSuite
import monocle.function.all._
import monocle.law.discipline.{OptionalTests, PrismTests}

class CharSpec extends MonocleSuite {

  checkAll("Char index bit", OptionalTests(index[Char, Int, Boolean](0)))

  checkAll("Char to Boolean ", PrismTests(charToBoolean))

}
