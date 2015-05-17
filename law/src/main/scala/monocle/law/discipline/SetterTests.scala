package monocle.law.discipline

import monocle.Setter
import monocle.law.SetterLaws
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Prop}
import org.typelevel.discipline.Laws

import scalaz.Equal

object SetterTests extends Laws {

  def apply[S: Arbitrary : Equal, A: Arbitrary : Equal](setter: Setter[S, A]): RuleSet = {
    val laws: SetterLaws[S, A] = new SetterLaws(setter)
    new SimpleRuleSet("Setter",
      "set idempotent" -> forAll( (s: S, a: A) => laws.setIdempotent(s, a)),
      "modify id = id" -> forAll( (s: S) => laws.modifyIdentity(s))
    )
  }

}
