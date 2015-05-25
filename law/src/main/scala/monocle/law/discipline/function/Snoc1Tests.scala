package monocle.law.discipline.function

import monocle.function._
import monocle.law.discipline.{LensTests, IsoTests}
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import scalaz.Equal
import scalaz.std.tuple._

object Snoc1Tests extends Laws {

  def apply[S, I, L](implicit iEq: Equal[I], iArb: Arbitrary[I],
                              lEq: Equal[L], lArb: Arbitrary[L],
                              sEq: Equal[S], sArb: Arbitrary[S],
                         evSnoc1: Snoc1[S, I, L]): RuleSet =
    new SimpleRuleSet("Snoc1",
      IsoTests(snoc1[S, I, L]).props ++
      LensTests(init[S, I, L]).props ++
      LensTests(last[S, I, L]).props: _*)

}