package monocle.law.discipline.function

import monocle.function._
import monocle.law.discipline.TraversalTests
import org.scalacheck.{Arbitrary, Prop}
import org.typelevel.discipline.Laws

import scalaz.Equal


object EachTests extends Laws {

  def apply[S, A](implicit aEq: Equal[A], aArb: Arbitrary[A],
                              sEq: Equal[S], sArb: Arbitrary[S],
                           evEach: Each[S, A]): RuleSet =
    new SimpleRuleSet("Each", TraversalTests(each[S, A]).props: _*)

}