package monocle.law.discipline.function

import monocle.function.Snoc1._
import monocle.function._
import monocle.law.discipline.{IsoTests, LensTests}
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import cats.Eq

object Snoc1Tests extends Laws {
  def apply[S: Eq: Arbitrary, I: Eq: Arbitrary, L: Eq: Arbitrary](implicit
    evSnoc1: Snoc1[S, I, L],
    arbSLSL: Arbitrary[((I, L)) => ((I, L))],
    arbSS: Arbitrary[I => I],
    arbLL: Arbitrary[L => L]
  ): RuleSet =
    new SimpleRuleSet(
      "Snoc1",
      IsoTests(snoc1[S, I, L]).props ++
        LensTests(init[S, I, L]).props ++
        LensTests(last[S, I, L]).props: _*
    )
}
