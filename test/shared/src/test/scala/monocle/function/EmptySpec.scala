package monocle.function

import monocle.MonocleSuite
import monocle.law.discipline.function.EmptyTests

import scala.annotation.nowarn

@nowarn
class EmptySpec extends MonocleSuite {
  implicit val slistEmpty: Empty[CList] = Empty.fromIso(CList.toList)

  checkAll("fromIso", EmptyTests[CList])
}
