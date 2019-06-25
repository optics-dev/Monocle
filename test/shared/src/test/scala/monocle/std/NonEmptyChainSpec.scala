package monocle.std

import cats.data.{Chain, NonEmptyChain}
import monocle.MonocleSuite
import monocle.law.discipline.{IsoTests, PrismTests}
import monocle.law.discipline.function._

class NonEmptyChainSpec extends MonocleSuite {
  import cats.laws.discipline.arbitrary._

  checkAll("necToAndOne", IsoTests(necToOneAnd[Int]))
  checkAll("optNecToChain", IsoTests(optNecToChain[Int]))
  checkAll("chainToNec", PrismTests(chainToNec[Int]))

  checkAll("each NonEmptyChain", EachTests[NonEmptyChain[Int], Int])
  checkAll("index NonEmptyChain", IndexTests[NonEmptyChain[Int], Int, Int])
  checkAll("filterIndex NonEmptyChain", FilterIndexTests[NonEmptyChain[Int], Int, Int])
  checkAll("reverse NonEmptyChain", ReverseTests[NonEmptyChain[Int]])
  checkAll("cons1 NonEmptyChain", Cons1Tests[NonEmptyChain[Int], Int, Chain[Int]])
  checkAll("snoc1 NonEmptyChain", Snoc1Tests[NonEmptyChain[Int], Chain[Int], Int])
}
