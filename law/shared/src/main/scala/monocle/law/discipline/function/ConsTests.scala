package monocle.law.discipline.function

import monocle.function.Cons._
import monocle.function._
import monocle.law.discipline.{OptionalTests, PrismTests}
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import scalaz.Equal
import scalaz.std.tuple._

object ConsTests extends Laws {

  def apply[S: Equal : Arbitrary, A: Equal : Arbitrary](implicit evCons: Cons[S, A],
     arbASAS: Arbitrary[((A,S)) => ((A,S))], arbAA: Arbitrary[A => A], arbSS: Arbitrary[S => S]): RuleSet =
    new SimpleRuleSet("Cons",
      PrismTests(cons[S, A]).props ++
      OptionalTests(headOption[S, A]).props ++
      OptionalTests(tailOption[S, A]).props: _*)

}