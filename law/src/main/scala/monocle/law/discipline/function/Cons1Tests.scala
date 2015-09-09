package monocle.law.discipline.function

import monocle.function.Cons1._
import monocle.function._
import monocle.law.discipline.{IsoTests, LensTests}
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import scalaz.Equal
import scalaz.std.tuple._

object Cons1Tests extends Laws {

  def apply[S, H, T](implicit hEq: Equal[H], hArb: Arbitrary[H],
                              tEq: Equal[T], tArb: Arbitrary[T],
                              sEq: Equal[S], sArb: Arbitrary[S],
                         evCons1: Cons1[S, H, T]): RuleSet =
    new SimpleRuleSet("Cons1",
      IsoTests(cons1[S, H, T]).props ++
      LensTests(head[S, H, T]).props ++
      LensTests(tail[S, H, T]).props: _*)
}