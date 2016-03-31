package monocle.function

import monocle.MonocleSuite
import monocle.law.discipline.function.{FilterIndexTests, AtTests}

class FilterIndexSpec extends MonocleSuite {

  implicit def mmapFilterIndex[K, V]: FilterIndex[MMap[K, V], K, V] =
    FilterIndex.fromIso(MMap.toMap)

  checkAll("fromIso", FilterIndexTests.evenIndex[MMap[Int, String], String])

}
