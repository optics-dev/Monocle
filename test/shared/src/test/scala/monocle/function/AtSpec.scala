package monocle.function

import cats.kernel.Eq
import monocle.MonocleSuite
import monocle.law.discipline.function.AtTests

import scala.collection.immutable.ListMap

class AtSpec extends MonocleSuite {
  implicit val eqListMap: Eq[ListMap[Int, String]] = Eq.fromUniversalEquals

  implicit def mmapAt[K, V]: At[MMap[K, V], K, Option[V]] =
    At.fromIso(MMap.toMap)

  checkAll("fromIso", AtTests[MMap[Int, String], Int, Option[String]])

  checkAll("ListMap", AtTests[ListMap[Int, String], Int, Option[String]])

  test("at creates a Lens from a Map, SortedMap to an optional value") {
    val map = Map("One" -> 1, "Two" -> 2)
    assertEquals(map.focus().at("One").replace(Some(-1)), Map("One" -> -1, "Two" -> 2))
    assertEquals(map.focus().at("Two").get, Some(2))
  }
}
