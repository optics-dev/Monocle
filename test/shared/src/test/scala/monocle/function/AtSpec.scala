package monocle.function

import cats.kernel.Eq
import monocle.{Lens, MonocleSuite}
import monocle.law.discipline.function.AtTests

import scala.collection.immutable.ListMap

class AtSpec extends MonocleSuite {
  implicit val eqListMap: Eq[ListMap[Int, String]] = Eq.fromUniversalEquals

  implicit def mmapAt[K, V]: At[MMap[K, V], K, Option[V]] =
    At.fromIso(MMap.toMap)

  checkAll("fromIso", AtTests[MMap[Int, String], Int, Option[String]])

  checkAll("ListMap", AtTests[ListMap[Int, String], Int, Option[String]])

  def mapDefaultTo0(index: String): Lens[Map[String, Int], Int] =
    atOrElse(index)(0)

  test("atOrElse") {
    val counters = Map("id1" -> 4, "id2" -> 2)

    assert(mapDefaultTo0("id1").get(counters) == 4)
    assert(mapDefaultTo0("id3").get(counters) == 0)

    assert(mapDefaultTo0("id1").modify(_ + 1)(counters) == Map("id1" -> 5, "id2" -> 2))
    assert(
      mapDefaultTo0("id3")
        .modify(_ + 1)(counters) == Map("id1" -> 4, "id2" -> 2, "id3" -> 1)
    )
  }

  test("atOrElse can break get-set property") {
    assert(mapDefaultTo0("id").set(0)(Map("id" -> 0)) == Map.empty)
  }
}
