package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.TraversalTests
import monocle.law.discipline.function._

class LazyListSpec extends MonocleSuite {
  checkAll("reverse LazyList", ReverseTests[LazyList[Int]])
  checkAll("empty LazyList", EmptyTests[LazyList[Int]])
  checkAll("cons LazyList", ConsTests[LazyList[Int], Int])
  checkAll("snoc LazyList", SnocTests[LazyList[Int], Int])
  checkAll("each LazyList", EachTests[LazyList[Int], Int])
  checkAll("index LazyList", IndexTests[LazyList[Int], Int, Int])
  checkAll("filterIndex LazyList", FilterIndexTests[LazyList[Int], Int, Int])

  checkAll("plated LazyList", TraversalTests(plate[LazyList[Int]]))
}
