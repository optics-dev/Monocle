package monocle.law.discipline.function

import monocle.function.Snoc._
import monocle.function._
import monocle.law.discipline.{OptionalTests, PrismTests}
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import cats.Eq
import cats.instances.tuple._

@deprecated("no replacement", since = "3.0.0-M1")
object SnocTests extends Laws {
  def apply[S: Eq: Arbitrary, A: Eq: Arbitrary](implicit
    evSnoc: Snoc[S, A],
    arbASAS: Arbitrary[((S, A)) => ((S, A))],
    arbAA: Arbitrary[A => A],
    arbSS: Arbitrary[S => S]
  ): RuleSet =
    new SimpleRuleSet(
      "Snoc",
      PrismTests(snoc[S, A]).props ++
        OptionalTests(lastOption[S, A]).props ++
        OptionalTests(initOption[S, A]).props: _*
    )
}
