package monocle.law.discipline

import monocle.Lens
import monocle.law.LensLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

import scalaz.Equal

object LensTests extends Laws {

  def apply[S: Arbitrary : Equal, A: Arbitrary : Equal](lens: Lens[S, A]): RuleSet =
    apply[S, A, Unit](_ => lens)

  def apply[S: Arbitrary : Equal, A: Arbitrary : Equal, I: Arbitrary](f: I => Lens[S, A]): RuleSet = {
    def laws(i: I) = LensLaws(f(i))
    new SimpleRuleSet("Lens",
      "set what you get"  -> forAll( (s: S, i: I) => laws(i).getSet(s)),
      "get what you set"  -> forAll( (s: S, a: A, i: I) => laws(i).setGet(s, a)),
      "set idempotent"   -> forAll( (s: S, a: A, i: I) => laws(i).setIdempotent(s, a)),
      "modify id = id"    -> forAll( (s: S, i: I) => laws(i).modifyIdentity(s)),
      "compose modify"    -> forAll( (s: S, g: A => A, h: A => A, i: I) => laws(i).composeModify(s, g, h)),
      "consistent set with modify"      -> forAll( (s: S, a: A, i: I) => laws(i).consistentSetModify(s, a)),
      "consistent modify with modifyId" -> forAll( (s: S, g: A => A, i: I) => laws(i).consistentModifyModifyId(s, g)),
      "consistent get with modifyId"    -> forAll( (s: S, i: I) => laws(i).consistentGetModifyId(s))
    )
  }

}
