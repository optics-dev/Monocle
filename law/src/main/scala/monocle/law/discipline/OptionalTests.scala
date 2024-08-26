package monocle.law.discipline

import monocle._
import monocle.law.OptionalLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

import cats.Eq

object OptionalTests extends Laws {
  def apply[S: Arbitrary: Eq, A: Arbitrary: Eq](optional: Optional[S, A])(implicit arbAA: Arbitrary[A => A]): RuleSet =
    apply[S, A, Unit](_ => optional)

  def apply[S: Arbitrary: Eq, A: Arbitrary: Eq, I: Arbitrary](
    f: I => Optional[S, A]
  )(implicit arbAA: Arbitrary[A => A]): RuleSet = {
    def laws(i: I) = OptionalLaws(f(i))
    new SimpleRuleSet(
      "Optional",
      "replace what you get"            -> forAll((s: S, i: I) => laws(i).getOptionReplace(s)),
      "get what you replace"            -> forAll((s: S, a: A, i: I) => laws(i).replaceGetOption(s, a)),
      "replace idempotent"              -> forAll((s: S, a: A, i: I) => laws(i).replaceIdempotent(s, a)),
      "modify id = id"                  -> forAll((s: S, i: I) => laws(i).modifyIdentity(s)),
      "compose modify"                  -> forAll((s: S, g: A => A, h: A => A, i: I) => laws(i).composeModify(s, g, h)),
      "consistent replace with modify"  -> forAll((s: S, a: A, i: I) => laws(i).consistentReplaceModify(s, a)),
      "consistent modify with modifyId" -> forAll((s: S, g: A => A, i: I) => laws(i).consistentModifyModifyId(s, g)),
      "consistent getOption with modifyId" -> forAll((s: S, i: I) => laws(i).consistentGetOptionModifyId(s))
    )
  }
}
