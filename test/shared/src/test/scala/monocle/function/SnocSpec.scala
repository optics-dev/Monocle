package monocle.function

import monocle.MonocleSuite
import monocle.law.discipline.function.SnocTests

class SnocSpec extends MonocleSuite {
  implicit val slistSnoc: Snoc[CList, Char] = Snoc.fromIso(CList.toList)

  checkAll("fromIso", SnocTests[CList, Char])
}
