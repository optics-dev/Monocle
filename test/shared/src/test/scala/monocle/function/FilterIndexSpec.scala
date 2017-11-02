package monocle.function

import cats.Order
import monocle.MonocleSuite
import monocle.law.discipline.function.FilterIndexTests

class FilterIndexSpec extends MonocleSuite {

  implicit def mmapFilterIndex[K: Order, V]: FilterIndex[MMap[K, V], K, V] =
    FilterIndex.fromIso(MMap.toSortedMap)

  checkAll("fromIso", FilterIndexTests[MMap[Int, String], Int, String])

}
