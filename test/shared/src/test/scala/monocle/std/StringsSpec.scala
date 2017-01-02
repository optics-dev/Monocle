package monocle.std

import java.util.UUID

import monocle.MonocleSuite
import monocle.function.Plated._
import monocle.law.discipline.function._
import monocle.law.discipline.{IsoTests, PrismTests, TraversalTests}
import org.scalacheck.Arbitrary

import scalaz.Equal

class StringsSpec extends MonocleSuite {
  implicit def arbitraryUUID: Arbitrary[UUID] = Arbitrary(UUID.randomUUID)

  implicit def equalUUID: Equal[UUID] = (a1: UUID, a2: UUID) => a1 == a2

  implicit def arbitraryUUIDtoUUID: Arbitrary[UUID => UUID] = Arbitrary(
    for (
      a <- Arbitrary.arbLong.arbitrary;
      b <- Arbitrary.arbLong.arbitrary
    ) yield (x: UUID) => new UUID(a * x.getMostSignificantBits, b * x.getLeastSignificantBits)
  )

  checkAll("stringToList", IsoTests(stringToList))
  checkAll("reverse String", ReverseTests[String])
  checkAll("empty String", EmptyTests[String])
  checkAll("cons String", ConsTests[String, Char])
  checkAll("snoc String", SnocTests[String, Char])
  checkAll("each String", EachTests[String, Char])
  checkAll("index String", IndexTests[String, Int, Char])
  checkAll("filterIndex String", FilterIndexTests[String, Int, Char])

  checkAll("String to Boolean ", PrismTests(stringToBoolean))
  checkAll("String to Byte"    , PrismTests(stringToByte))
  checkAll("String to Int"     , PrismTests(stringToInt))
  checkAll("String to Long"    , PrismTests(stringToLong))
  checkAll("String to UUID"    , PrismTests(stringToUUID))

  checkAll("plated String", TraversalTests(plate[String]))

}
