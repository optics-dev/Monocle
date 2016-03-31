package monocle.law.discipline.function

import monocle.function.Cons._
import monocle.function._
import monocle.law.discipline.{OptionalTests, PrismTests}
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import scalaz.Equal
import scalaz.std.tuple._

object ConsTests extends Laws {

  def apply[S, A](implicit aEq: Equal[A], aArb: Arbitrary[A],
                           sEq: Equal[S], sArb: Arbitrary[S],
                           evCons: Cons[S, A]): RuleSet =
    new SimpleRuleSet("Cons",
      PrismTests(cons[S, A]).props ++
      OptionalTests(headOption[S, A]).props ++
      OptionalTests(tailOption[S, A]).props: _*)

}