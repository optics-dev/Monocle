package monocle.law.discipline.function

import monocle.function.FilterIndex._
import monocle.function._
import monocle.law.discipline.TraversalTests
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import scalaz.Equal

object FilterIndexTests extends Laws {

  def apply[S: Equal : Arbitrary, I, A: Equal : Arbitrary](implicit evFilterIndex: FilterIndex[S, I, A],
                                                                    arbAA: Arbitrary[A => A], arbIB: Arbitrary[I => Boolean]): RuleSet =
    new SimpleRuleSet("FilterIndex", TraversalTests(filterIndex(_: I => Boolean)(evFilterIndex)).props: _*)


}