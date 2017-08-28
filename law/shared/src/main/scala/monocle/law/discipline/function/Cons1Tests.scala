package monocle.law.discipline.function

import monocle.function.Cons1._
import monocle.function._
import monocle.law.discipline.{IsoTests, LensTests}
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import cats.{Eq => Equal}
import cats.instances.tuple._

object Cons1Tests extends Laws {

  def apply[S: Equal : Arbitrary, H: Equal : Arbitrary, T: Equal : Arbitrary](implicit evCons1: Cons1[S, H, T],
      arbHTHT: Arbitrary[((H,T)) => ((H,T))], arbHH: Arbitrary[H => H], arbTT: Arbitrary[T => T]): RuleSet =
    new SimpleRuleSet("Cons1",
      IsoTests(cons1[S, H, T]).props ++
      LensTests(head[S, H, T]).props ++
      LensTests(tail[S, H, T]).props: _*)
}
