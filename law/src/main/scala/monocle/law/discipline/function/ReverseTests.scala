package monocle.law.discipline.function

import monocle.function.Reverse._
import monocle.function._
import monocle.law.discipline.IsoTests
import org.scalacheck.{Arbitrary, Prop}
import org.typelevel.discipline.Laws

import cats.Eq

object ReverseTests extends Laws {
  def apply[S: Eq: Arbitrary](implicit evReverse: Reverse[S, S], arbSS: Arbitrary[S => S]): RuleSet =
    apply[S, S]

  def apply[S: Eq: Arbitrary, A: Eq: Arbitrary](implicit evReverse: Reverse[S, A], arbAA: Arbitrary[A => A]): RuleSet =
    new RuleSet {
      override def name: String                  = "Reverse"
      override def bases: Seq[(String, RuleSet)] = Nil
      override def parents: Seq[RuleSet]         = Nil
      override def props: Seq[(String, Prop)] =
        IsoTests(reverse[S, A]).props
    }
}
