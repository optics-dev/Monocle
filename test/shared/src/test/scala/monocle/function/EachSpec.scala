package monocle.function

import cats.kernel.Eq
import monocle.MonocleSuite
import monocle.law.discipline.function.EachTests

class EachSpec extends MonocleSuite {
  implicit val eqListMap: Eq[Map[String, String]] = Eq.fromUniversalEquals

  implicit val slistEach: Each[CList, Char] = Each.fromIso(CList.toList)

  checkAll("fromIso", EachTests[CList, Char])
}
