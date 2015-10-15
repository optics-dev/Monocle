package monocle.function

import monocle.law.discipline.function.ConsTests
import monocle.{Iso, MonocleSuite}

class ConsSpec extends MonocleSuite {

  implicit val slistCons: Cons[CList, Char] = Cons.fromIso(CList.toList)

  checkAll("fromIso", ConsTests[CList, Char])

}
