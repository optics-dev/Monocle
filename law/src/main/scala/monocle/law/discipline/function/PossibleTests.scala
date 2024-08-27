package monocle.law.discipline.function

import monocle.function.Possible._
import monocle.function._
import monocle.law.discipline.OptionalTests
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import cats.Eq

@deprecated("no replacement", since = "3.0.0-M1")
object PossibleTests extends Laws {
  def apply[S: Eq: Arbitrary, A: Eq: Arbitrary](implicit
    evPossible: Possible[S, A],
    arbAA: Arbitrary[A => A]
  ): RuleSet =
    new SimpleRuleSet("Possible", OptionalTests(possible[S, A]).props*)
}
