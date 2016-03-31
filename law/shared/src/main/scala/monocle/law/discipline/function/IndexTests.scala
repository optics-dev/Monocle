package monocle.law.discipline.function

import monocle.function.Index._
import monocle.function._
import monocle.law.discipline.OptionalTests
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import scalaz.Equal

object IndexTests extends Laws {

  def apply[S, I, A](i: I)(implicit aEq: Equal[A], aArb: Arbitrary[A],
                                    sEq: Equal[S], sArb: Arbitrary[S],
                                evIndex: Index[S, I, A]): RuleSet =
    new SimpleRuleSet("Index", OptionalTests(index(i)).props: _*)

  def defaultIntIndex[S, A](implicit aEq: Equal[A], aArb: Arbitrary[A],
                                     sEq: Equal[S], sArb: Arbitrary[S],
                                 evIndex: Index[S, Int, A]): RuleSet =
    apply[S, Int, A](2)

}