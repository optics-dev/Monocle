package monocle.law.discipline.function

import monocle.function.Index._
import monocle.function._
import monocle.law.discipline.OptionalTests
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import scalaz.Equal

object IndexTests extends Laws {

  def apply[S: Equal : Arbitrary, I : Arbitrary, A: Equal : Arbitrary](implicit evIndex: Index[S, I, A],
                                                                       arbAA: Arbitrary[A => A]): RuleSet =
    new SimpleRuleSet("Index", OptionalTests(index(_ : I)).props: _*)

}