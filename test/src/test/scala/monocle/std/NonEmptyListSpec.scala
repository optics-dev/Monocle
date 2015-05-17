package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function._

import scalaz.NonEmptyList


class NonEmptyListSpec extends MonocleSuite {

  checkAll("each NonEmptyList", EachTests[NonEmptyList[Int], Int])
  checkAll("index NonEmptyList", IndexTests.defaultIntIndex[NonEmptyList[Int], Int])
  checkAll("filterIndex NonEmptyList", FilterIndexTests.evenIndex[NonEmptyList[Int], Int])
  checkAll("reverse NonEmptyList", ReverseTests[NonEmptyList[Int]])
  checkAll("cons1 NonEmptyList", Cons1Tests[NonEmptyList[Int], Int, List[Int]])
  checkAll("snoc1 NonEmptyList", Snoc1Tests[NonEmptyList[Int], List[Int], Int])

}
