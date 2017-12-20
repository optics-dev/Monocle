package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function._
import scala.collection.immutable.SortedMap

class SortedMapSpec extends MonocleSuite {
  checkAll("at SortedMap", AtTests[SortedMap[Int, String], Int, Option[String]])
  checkAll("each SortedMap", EachTests[SortedMap[Int, String], String])
  checkAll("empty SortedMap", EmptyTests[SortedMap[Int, String]])
  checkAll("index SortedMap", IndexTests[SortedMap[Int, String], Int, String])
  checkAll("filterIndex SortedMap", FilterIndexTests[SortedMap[Int, Char], Int, Char])
}
