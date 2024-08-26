package monocle.law.discipline

import monocle.Prism
import monocle.law.PrismLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

import cats.Eq

object PrismTests extends Laws {
  def apply[S: Arbitrary: Eq, A: Arbitrary: Eq](prism: Prism[S, A])(implicit arbAA: Arbitrary[A => A]): RuleSet = {
    val laws: PrismLaws[S, A] = new PrismLaws(prism)
    new SimpleRuleSet(
      "Prism",
      "partial round trip one way"         -> forAll((s: S) => laws.partialRoundTripOneWay(s)),
      "round trip other way"               -> forAll((a: A) => laws.roundTripOtherWay(a)),
      "modify id = id"                     -> forAll((s: S) => laws.modifyIdentity(s)),
      "compose modify"                     -> forAll((s: S, f: A => A, g: A => A) => laws.composeModify(s, f, g)),
      "consistent replace with modify"     -> forAll((s: S, a: A) => laws.consistentReplaceModify(s, a)),
      "consistent modify with modifyId"    -> forAll((s: S, g: A => A) => laws.consistentModifyModifyId(s, g)),
      "consistent getOption with modifyId" -> forAll((s: S) => laws.consistentGetOptionModifyId(s))
    )
  }
}
