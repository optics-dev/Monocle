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

  def apply[S: Arbitrary : Equal, A: Arbitrary : Equal](traversal: Traversal[S, A]): RuleSet = {
    val laws: TraversalLaws[S, A] = new TraversalLaws(traversal)
    new SimpleRuleSet("Traversal",
      "get what you set"  -> forAll( (s: S, a: A) => laws.setGetAll(s, a)),
      "set idempotent"    -> forAll( (s: S, a: A) => laws.setIdempotent(s, a)),
      "modify id = id"    -> forAll( (s: S) => laws.modifyIdentity(s)),
      "modifyF Id = Id"   -> forAll( (s: S) => laws.modifyFId(s)),
      "headOption"        -> forAll( (s: S) => laws.headOption(s)),
      "compose modify"    -> forAll( (s: S, f: A => A, g: A => A) => laws.composeModify(s, f, g)),
      "consistent modify" -> forAll( (s: S, a: A) => laws.consistentModify(s, a))
    )
  }

}
