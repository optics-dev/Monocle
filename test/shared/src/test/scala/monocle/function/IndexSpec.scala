package monocle.function

import monocle.MonocleSuite
import monocle.law.discipline.function.IndexTests

class IndexSpec extends MonocleSuite {

  implicit def mmapIndex[K, V]: Index[MMap[K, V], K, V] = Index.fromIso(MMap.toMap)

  checkAll("fromIso", IndexTests.defaultIntIndex[MMap[Int, String], String])

}
