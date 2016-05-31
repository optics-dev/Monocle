package monocle.law.discipline

import monocle.Traversal
import monocle.law.TraversalLaws
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Prop}
import org.typelevel.discipline.Laws

import scalaz.Equal
import scalaz.std.list._
import scalaz.std.option._

object TraversalTests extends Laws {

  def apply[S: Arbitrary : Equal, A: Arbitrary : Equal](traversal: Traversal[S, A])(implicit arbAA: Arbitrary[A => A]): RuleSet = {
    val laws: TraversalLaws[S, A] = new TraversalLaws(traversal)
    new SimpleRuleSet("Traversal",
      "headOption"        -> forAll( (s: S) => laws.headOption(s)),
      "get what you set"  -> forAll( (s: S, f: A => A) => laws.modifyGetAll(s, f)),
      "set idempotent"   -> forAll( (s: S, a: A) => laws.setIdempotent(s, a)),
      "modify id = id"    -> forAll( (s: S) => laws.modifyIdentity(s)),
      "compose modify"    -> forAll( (s: S, f: A => A, g: A => A) => laws.composeModify(s, f, g))
    )
  }

}
