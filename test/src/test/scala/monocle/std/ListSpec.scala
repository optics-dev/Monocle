package monocle.std

import monocle.{MonocleSuite, Plated}
import monocle.law.discipline.{IsoTests, TraversalTests}
import monocle.law.discipline.function._

class ListSpec extends MonocleSuite {
  checkAll("listToVector", IsoTests(listToVector[Int]))

  checkAll("reverse List", ReverseTests[List[Int]])
  checkAll("empty List", EmptyTests[List[Int]])
  checkAll("cons List", ConsTests[List[Int], Int])
  checkAll("snoc List", SnocTests[List[Int], Int])
  checkAll("each List", EachTests[List[Int], Int])
  checkAll("index List", IndexTests.defaultIntIndex[List[Int], Int])
  checkAll("filterIndex List", FilterIndexTests.evenIndex[List[Int], Int])

  checkAll("plated List", TraversalTests(Plated.plate[List[Int]]))
}
