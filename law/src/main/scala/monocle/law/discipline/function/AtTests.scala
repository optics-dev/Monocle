package monocle.law.discipline.function

import monocle.function._
import monocle.law.discipline.LensTests
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import scalaz.Equal
import scalaz.std.option._

object AtTests extends Laws {

  def apply[S, I, A](i: I)(implicit aEq: Equal[A], aArb: Arbitrary[A],
                                    sEq: Equal[S], sArb: Arbitrary[S],
                                   evAt: At[S, I, A]): RuleSet =
    new SimpleRuleSet("At", LensTests(at(i)).props: _*)

  def defaultIntIndex[S, A](implicit aEq: Equal[A], aArb: Arbitrary[A],
                                     sEq: Equal[S], sArb: Arbitrary[S],
                                    evAt: At[S, Int, A]): RuleSet =
    apply[S, Int, A](2)

}