package monocle.function

import monocle.law.discipline.function.ConsTests
import monocle.MonocleSuite

import scala.annotation.nowarn

@nowarn
class ConsSpec extends MonocleSuite {
  implicit val slistCons: Cons[CList, Char] = Cons.fromIso(CList.toList)

  checkAll("fromIso", ConsTests[CList, Char])
}
