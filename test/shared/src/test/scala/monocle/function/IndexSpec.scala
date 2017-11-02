package monocle.function

import cats.Order
import monocle.MonocleSuite
import monocle.law.discipline.function.IndexTests

class IndexSpec extends MonocleSuite {

  implicit def mmapIndex[K: Order, V]: Index[MMap[K, V], K, V] = Index.fromIso(MMap.toSortedMap)

  checkAll("fromIso", IndexTests[MMap[Int, String], Int, String])

}
