package monocle.function

import monocle.MonocleSuite
import monocle.law.discipline.function.AtTests

class AtSpec extends MonocleSuite {

  implicit def mmapAt[K, V]: At[MMap[K, V], K, Option[V]] = At.fromIso(MMap.toMap)

  checkAll("fromIso", AtTests.defaultIntIndex[MMap[Int, String], Option[String]])

}
