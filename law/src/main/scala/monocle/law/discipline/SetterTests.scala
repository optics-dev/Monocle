package monocle.law.discipline

import monocle.Setter
import monocle.law.SetterLaws
import org.scalacheck.Prop._
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import cats.Eq

object SetterTests extends Laws {
  def apply[S: Arbitrary: Eq, A: Arbitrary: Eq](setter: Setter[S, A])(implicit arbAA: Arbitrary[A => A]): RuleSet = {
    val laws: SetterLaws[S, A] = new SetterLaws(setter)
    new SimpleRuleSet(
      "Setter",
      "replace idempotent"             -> forAll((s: S, a: A) => laws.replaceIdempotent(s, a)),
      "modify id = id"                 -> forAll((s: S) => laws.modifyIdentity(s)),
      "compose modify"                 -> forAll((s: S, f: A => A, g: A => A) => laws.composeModify(s, f, g)),
      "consistent replace with modify" -> forAll((s: S, a: A) => laws.consistentReplaceModify(s, a))
    )
  }
}
