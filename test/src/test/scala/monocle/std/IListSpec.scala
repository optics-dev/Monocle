package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function.{ConsTests, EmptyTests, ReverseTests}

import scalaz.IList

class IListSpec extends MonocleSuite {
  checkAll("IList Reverse ", ReverseTests[IList[Char]])
  checkAll("IList Empty", EmptyTests[IList[Char]])
  checkAll("IList Cons", ConsTests[IList[Char], Char])
  checkAll("IList Snoc", ConsTests[IList[Char], Char])
}
