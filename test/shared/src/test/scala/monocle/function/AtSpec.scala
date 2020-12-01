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
    assertEquals((map applyLens at("One")).replace(Some(-1)), Map("One" -> -1, "Two" -> 2))
    assertEquals((map applyLens at("Two")).get, Some(2))
  }

  test("at for tuples") {
    val tuple2 = (true, "hello")
    val tuple3 = (true, "hello", 5)

    assertEquals((tuple2 applyLens at(1)).get, true)
    assertEquals((tuple2 applyLens at(2)).get, "hello")

    assertEquals((tuple3 applyLens at(1)).get, true)
    assertEquals((tuple3 applyLens at(2)).get, "hello")
    assertEquals((tuple3 applyLens at(3)).get, 5)
  }
}
