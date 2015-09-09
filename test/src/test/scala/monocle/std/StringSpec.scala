package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function._
import monocle.law.discipline.{IsoTests, PrismTests}

class StringSpec extends MonocleSuite {
  checkAll("stringToList", IsoTests(stringToList))
  checkAll("reverse String", ReverseTests[String])
  checkAll("empty String", EmptyTests[String])
  checkAll("cons String", ConsTests[String, Char])
  checkAll("snoc String", SnocTests[String, Char])
  checkAll("each String", EachTests[String, Char])
  checkAll("index String", IndexTests.defaultIntIndex[String, Char])
  checkAll("filterIndex String", FilterIndexTests.evenIndex[String, Char])

  checkAll("String to Boolean ", PrismTests(stringToBoolean))
  checkAll("String to Byte"    , PrismTests(stringToByte))
  checkAll("String to Int"     , PrismTests(stringToInt))
  checkAll("String to Long"    , PrismTests(stringToLong))



}
