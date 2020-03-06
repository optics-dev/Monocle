package monocle.law.discipline

import monocle.Traversal
import monocle.law.TraversalLaws
import org.scalacheck.Prop._
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import cats.Eq
import cats.instances.list._
import cats.instances.option._

object TraversalTests extends Laws {
  def apply[S: Arbitrary: Eq, A: Arbitrary: Eq](
    traversal: Traversal[S, A]
  )(implicit arbAA: Arbitrary[A => A]): RuleSet =
    apply[S, A, Unit](_ => traversal)

  def apply[S: Arbitrary: Eq, A: Arbitrary: Eq, I: Arbitrary](
    f: I => Traversal[S, A]
  )(implicit arbAA: Arbitrary[A => A]): RuleSet = {
    def laws(i: I): TraversalLaws[S, A] = new TraversalLaws(f(i))
    new SimpleRuleSet(
      "Traversal",
      "headOption"       -> forAll((s: S, i: I) => laws(i).headOption(s)),
      "get what you set" -> forAll((s: S, f: A => A, i: I) => laws(i).modifyGetAll(s, f)),
      "set idempotent"   -> forAll((s: S, a: A, i: I) => laws(i).setIdempotent(s, a)),
      "modify id = id"   -> forAll((s: S, i: I) => laws(i).modifyIdentity(s)),
      "compose modify"   -> forAll((s: S, f: A => A, g: A => A, i: I) => laws(i).composeModify(s, f, g))
    )
  }
}
