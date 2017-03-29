package monocle.function

import monocle.MonocleSuite
import monocle.law.discipline.function.FilterIndexTests

class FilterIndexSpec extends MonocleSuite {

  implicit def mmapFilterIndex[K, V]: FilterIndex[MMap[K, V], K, V] =
    FilterIndex.fromIso(MMap.toMap)

  checkAll("fromIso", FilterIndexTests[MMap[Int, String], Int, String])

}
