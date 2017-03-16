package monocle.law.discipline.function

import monocle.function.Empty._
import monocle.function._
import monocle.law.discipline.PrismTests
import org.scalacheck.{Arbitrary, Prop}
import org.typelevel.discipline.Laws

import cats.{Eq => Equal}
import cats.instances.unit._

object EmptyTests extends Laws {

  def apply[S: Arbitrary : Equal : Empty]: RuleSet = new RuleSet {
    override def name: String = "Empty"
    override def bases: Seq[(String, RuleSet)] = Nil
    override def parents: Seq[RuleSet] = Nil
    override def props: Seq[(String, Prop)] =
      PrismTests(empty[S]).props
  }
}
