package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law._
import org.specs2.scalaz.Spec

import scalaz.NonEmptyList


class NonEmptyListSpec extends Spec {

  checkAll("each NonEmptyList", TraversalLaws(each[NonEmptyList[Int], Int]))
  checkAll("index NonEmptyList", OptionalLaws(index[NonEmptyList[Int], Int, Int](1)))
  checkAll("filterIndex NonEmptyList", TraversalLaws(filterIndex[NonEmptyList[Int], Int, Int](_ % 2 == 0)))
  checkAll("reverse NonEmptyList", IsoLaws(reverse[NonEmptyList[Int], NonEmptyList[Int]]))
  checkAll("head NonEmptyList", LensLaws(head[NonEmptyList[Int], Int]))
  checkAll("tail NonEmptyList", LensLaws(tail[NonEmptyList[Int], List[Int]]))
  checkAll("cons NonEmptyList", PrismLaws(cons[NonEmptyList[Int], Int]))
  checkAll("snoc NonEmptyList", PrismLaws(snoc[NonEmptyList[Int], Int]))

}
