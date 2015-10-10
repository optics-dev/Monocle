package monocle.function

import monocle.MonocleSuite
import monocle.law.discipline.function.EachTests

class EachSpec extends MonocleSuite {

  implicit val slistEach: Each[CList, Char] = Each.fromIso(CList.toList)

  checkAll("fromIso", EachTests[CList, Char])

}
