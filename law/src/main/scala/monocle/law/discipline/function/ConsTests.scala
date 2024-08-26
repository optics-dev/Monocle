package monocle.law.discipline.function

import monocle.function.Cons._
import monocle.function._
import monocle.law.discipline.{OptionalTests, PrismTests}
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import cats.Eq
import cats.instances.tuple._

@deprecated("no replacement", since = "3.0.0-M1")
object ConsTests extends Laws {
  def apply[S: Eq: Arbitrary, A: Eq: Arbitrary](implicit
    evCons: Cons[S, A],
    arbASAS: Arbitrary[((A, S)) => ((A, S))],
    arbAA: Arbitrary[A => A],
    arbSS: Arbitrary[S => S]
  ): RuleSet =
    new SimpleRuleSet(
      "Cons",
      (PrismTests(cons[S, A]).props ++
        OptionalTests(headOption[S, A]).props ++
        OptionalTests(tailOption[S, A]).props) *
    )
}
