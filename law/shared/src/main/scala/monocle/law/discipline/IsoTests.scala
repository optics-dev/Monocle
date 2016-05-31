package monocle.law.discipline

import monocle.Iso
import monocle.law.IsoLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

import scalaz.Equal

object IsoTests extends Laws {

  def apply[S: Arbitrary : Equal, A: Arbitrary : Equal](iso: Iso[S, A]): RuleSet = {
    val laws = new IsoLaws(iso)
    new SimpleRuleSet("Iso",
      "round trip one way"   -> forAll( (s: S) => laws.roundTripOneWay(s)),
      "round trip other way" -> forAll( (a: A) => laws.roundTripOtherWay(a)),
      "modify id = id"       -> forAll( (s: S) => laws.modifyIdentity(s)),
      "compose modify"       -> forAll( (s: S, f: A => A, g: A => A) => laws.composeModify(s, f, g)),
      "consistent set with modify"      -> forAll( (s: S, a: A) => laws.consistentSetModify(s, a)),
      "consistent modify with modifyId" -> forAll( (s: S, f: A => A) => laws.consistentModifyModifyId(s, f)),
      "consistent get with modifyId"    -> forAll( (s: S) => laws.consistentGetModifyId(s))
    )
  }

}