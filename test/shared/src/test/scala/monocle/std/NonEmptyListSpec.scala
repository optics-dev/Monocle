package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.IsoTests
import monocle.law.discipline.function._
import cats.data.NonEmptyList

import scala.annotation.nowarn

class NonEmptyListSpec extends MonocleSuite {
  import cats.laws.discipline.arbitrary._

  checkAll("nelToAndOne", IsoTests(nelToOneAnd[Int]))
  checkAll("optNelToList", IsoTests(optNelToList[Int]))

  checkAll("each NonEmptyList", EachTests[NonEmptyList[Int], Int])
  checkAll("index NonEmptyList", IndexTests[NonEmptyList[Int], Int, Int])
  checkAll("filterIndex NonEmptyList", FilterIndexTests[NonEmptyList[Int], Int, Int])
  checkAll("reverse NonEmptyList", ReverseTests[NonEmptyList[Int]]): @nowarn
  checkAll("cons1 NonEmptyList", Cons1Tests[NonEmptyList[Int], Int, List[Int]])
  checkAll("snoc1 NonEmptyList", Snoc1Tests[NonEmptyList[Int], List[Int], Int])
}
