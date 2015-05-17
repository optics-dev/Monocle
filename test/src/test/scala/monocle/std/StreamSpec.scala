package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function._

class StreamSpec extends MonocleSuite {

  checkAll("reverse Stream", ReverseTests[Stream[Int]])
  checkAll("empty Stream", EmptyTests[Stream[Int]])
  checkAll("cons Stream", ConsTests[Stream[Int], Int])
  checkAll("snoc Stream", SnocTests[Stream[Int], Int])
  checkAll("each Stream", EachTests[Stream[Int], Int])
  checkAll("index Stream", IndexTests.defaultIntIndex[Stream[Int], Int])
  checkAll("filterIndex Stream", FilterIndexTests.evenIndex[Stream[Int], Int])

}
