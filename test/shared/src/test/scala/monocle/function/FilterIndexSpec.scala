package monocle.function

import cats.Order
import monocle.MonocleSuite
import monocle.law.discipline.function.FilterIndexTests

class FilterIndexSpec extends MonocleSuite {
  implicit def mmapFilterIndex[K: Order, V]: FilterIndex[MSorteMap[K, V], K, V] =
    FilterIndex.fromIso(MSorteMap.toSortedMap)

  checkAll("fromIso", FilterIndexTests[MSorteMap[Int, String], Int, String])
}
