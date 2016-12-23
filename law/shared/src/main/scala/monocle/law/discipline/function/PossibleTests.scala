package monocle.law.discipline.function

import monocle.function.Possible._
import monocle.function._
import monocle.law.discipline.OptionalTests
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import scalaz.Equal


object PossibleTests extends Laws {

  def apply[S: Equal : Arbitrary, A: Equal : Arbitrary](implicit evPossible: Possible[S, A], arbAA: Arbitrary[A => A]): RuleSet =
    new SimpleRuleSet("Possible", OptionalTests(possible[S, A]).props: _*)

}
