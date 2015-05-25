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
      "modifyF Id = Id"      -> forAll( (s: S) => laws.modifyFId(s))
    )
  }

}