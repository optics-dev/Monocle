package monocle.std

import monocle.MonocleSuite
import monocle.function.Plated._
import monocle.law.discipline.function._
import monocle.law.discipline.{IsoTests, PrismTests, TraversalTests}

import scala.annotation.nowarn

class StringsSpec extends MonocleSuite {
  checkAll("stringToList", IsoTests(stringToList))
  checkAll("reverse String", ReverseTests[String]): @nowarn
  checkAll("empty String", EmptyTests[String]): @nowarn
  checkAll("cons String", ConsTests[String, Char]): @nowarn
  checkAll("snoc String", SnocTests[String, Char]): @nowarn
  checkAll("each String", EachTests[String, Char])
  checkAll("index String", IndexTests[String, Int, Char])
  checkAll("filterIndex String", FilterIndexTests[String, Int, Char])

  checkAll("String to Boolean ", PrismTests(stringToBoolean))
  checkAll("String to Byte", PrismTests(stringToByte))
  checkAll("String to Int", PrismTests(stringToInt))
  checkAll("String to Long", PrismTests(stringToLong))
  checkAll("String to UUID", PrismTests(stringToUUID))
  // checkAll("String to URI", PrismTests(stringToURI))

  checkAll("plated String", TraversalTests(plate[String]))
}
