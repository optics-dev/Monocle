package monocle.law.discipline.function

import monocle.function.At._
import monocle.function._
import monocle.law.discipline.LensTests
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import scalaz.Equal

object AtTests extends Laws {

  def apply[S, I, A](implicit aEq: Equal[A], aArb: Arbitrary[A],
                              sEq: Equal[S], sArb: Arbitrary[S],
                              iArb: Arbitrary[I], evAt: At[S, I, A]): RuleSet = {
    new SimpleRuleSet("At", LensTests(at(_: I)).props: _*)
  }

}