package monocle.std

import cats.data.NonEmptyVector
import monocle.MonocleSuite
import monocle.law.discipline.IsoTests
import monocle.law.discipline.function._

import scala.{Vector => IVector}

class NonEmptyVectorSpec extends MonocleSuite {
  checkAll("nevToAndOne", IsoTests(nevToOneAnd[Int]))
  checkAll("optNevToVector", IsoTests(optNevToVector[Int]))

  checkAll("each NonEmptyVector", EachTests[NonEmptyVector[Int], Int])
  checkAll("index NonEmptyVector", IndexTests[NonEmptyVector[Int], Int, Int])
  checkAll("filterIndex NonEmptyVector", FilterIndexTests[NonEmptyVector[Int], Int, Int])
  checkAll("reverse NonEmptyVector", ReverseTests[NonEmptyVector[Int]])
  checkAll("cons1 NonEmptyVector", Cons1Tests[NonEmptyVector[Int], Int, IVector[Int]])
  checkAll("snoc1 NonEmptyVector", Snoc1Tests[NonEmptyVector[Int], IVector[Int], Int])
}
