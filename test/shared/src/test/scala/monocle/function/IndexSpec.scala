package monocle.function

import cats.kernel.Eq
import monocle.MonocleSuite
import monocle.law.discipline.function.IndexTests

import scala.collection.immutable.ListMap

class IndexSpec extends MonocleSuite {

  implicit val eqListMap: Eq[ListMap[Int, String]] = Eq.fromUniversalEquals

  implicit def mmapIndex[K, V]: Index[MMap[K, V], K, V] = Index.fromIso(MMap.toMap)

  checkAll("fromIso", IndexTests[MMap[Int, String], Int, String])

  checkAll("ListMap", IndexTests[ListMap[Int, String], Int, String])

}
