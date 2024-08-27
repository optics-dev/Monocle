package monocle.law.discipline.function

import monocle.function.FilterIndex._
import monocle.function._
import monocle.law.discipline.TraversalTests
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import cats.Eq

object FilterIndexTests extends Laws {
  def apply[S: Eq: Arbitrary, I, A: Eq: Arbitrary](implicit
    evFilterIndex: FilterIndex[S, I, A],
    arbAA: Arbitrary[A => A],
    arbIB: Arbitrary[I => Boolean]
  ): RuleSet =
    new SimpleRuleSet("FilterIndex", TraversalTests(filterIndex(_: I => Boolean)(evFilterIndex)).props*)
}
