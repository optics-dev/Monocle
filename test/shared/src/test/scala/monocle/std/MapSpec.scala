package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function._

class MapSpec extends MonocleSuite {
  checkAll("at Map", AtTests[Map[Int, String], Int, Option[String]])
  checkAll("each Map", EachTests[Map[Int, String], String])
  checkAll("empty Map", EmptyTests[Map[Int, String]])
  checkAll("index Map", IndexTests[Map[Int, String], Int, String])
  checkAll("filterIndex Map", FilterIndexTests.evenIndex[Map[Int, Char], Char])
}
