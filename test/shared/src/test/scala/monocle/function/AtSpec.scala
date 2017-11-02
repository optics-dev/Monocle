package monocle.function

import cats.Order
import monocle.MonocleSuite
import monocle.law.discipline.function.AtTests
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import scala.collection.immutable.SortedMap

class AtSpec extends MonocleSuite with GeneratorDrivenPropertyChecks {

  implicit def mmapAt[K: Order, V]: At[MMap[K, V], K, Option[V]] = At.fromIso(MMap.toSortedMap)

  checkAll("fromIso", AtTests[MMap[Int, String], Int, Option[String]])

  test("remove deletes a key") {

    val mapAndIndexGen: Gen[(SortedMap[Int, String], Int)] = for {
      m <- Arbitrary.arbitrary[SortedMap[Int, String]]
      i <- if(m.isEmpty) Arbitrary.arbInt.arbitrary
      else Gen.frequency(
        (8, Gen.oneOf(m.keys.toList)),
        (2, Arbitrary.arbInt.arbitrary))
    } yield (m, i)

    forAll(mapAndIndexGen) { case (m, i) =>
      remove(i)(m) should be (m - i)
    }
  }

}
