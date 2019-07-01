package monocle.law.discipline.function

import monocle.function.Index._
import monocle.function._
import monocle.law.discipline.OptionalTests
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import cats.Eq

object IndexTests extends Laws {

  def apply[S: Eq : Arbitrary, I : Arbitrary, A: Eq : Arbitrary](implicit evIndex: Index[S, I, A],
                                                                          arbAA: Arbitrary[A => A]): RuleSet =
    new SimpleRuleSet("Index", OptionalTests(index(_ : I)(evIndex)).props: _*)

}
