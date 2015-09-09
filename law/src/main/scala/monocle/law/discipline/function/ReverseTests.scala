package monocle.law.discipline.function

import monocle.function.Reverse._
import monocle.function._
import monocle.law.discipline.IsoTests
import org.scalacheck.{Arbitrary, Prop}
import org.typelevel.discipline.Laws

import scalaz.Equal

object ReverseTests extends Laws {

  def apply[S, A](implicit aEq: Equal[A], aArb: Arbitrary[A],
                           sEq: Equal[S], sArb: Arbitrary[S],
                           evReverse: Reverse[S, A]): RuleSet = new RuleSet {
    override def name: String = "Reverse"
    override def bases: Seq[(String, RuleSet)] = Nil
    override def parents: Seq[RuleSet] = Nil
    override def props: Seq[(String, Prop)] =
      IsoTests(reverse[S, A]).props
  }

  def apply[S](implicit sEq: Equal[S], sArb: Arbitrary[S], evReverse: Reverse[S, S]): RuleSet =
    apply[S, S]
}