package monocle.law.discipline

import monocle.Setter
import monocle.law.SetterLaws
import org.scalacheck.Prop._
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws

import cats.{Eq => Equal}

object SetterTests extends Laws {

  def apply[S: Arbitrary : Equal, A: Arbitrary : Equal](setter: Setter[S, A])(implicit arbAA: Arbitrary[A => A]): RuleSet = {
    val laws: SetterLaws[S, A] = new SetterLaws(setter)
    new SimpleRuleSet("Setter",
      "set idempotent" -> forAll( (s: S, a: A) => laws.setIdempotent(s, a)),
      "modify id = id" -> forAll( (s: S) => laws.modifyIdentity(s)),
      "compose modify" -> forAll( (s: S, f: A => A, g: A => A) => laws.composeModify(s, f, g)),
      "consistent set with modify" -> forAll( (s: S, a: A) => laws.consistentSetModify(s, a))
    )
  }

}
