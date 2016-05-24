package monocle.law.discipline

import monocle.Prism
import monocle.law.PrismLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

import scalaz.Equal
import scalaz.std.option._

object PrismTests extends Laws {

  def apply[S: Arbitrary : Equal, A: Arbitrary : Equal](prism: Prism[S, A]): RuleSet = {
    val laws: PrismLaws[S, A] = new PrismLaws(prism)
    new SimpleRuleSet("Prism",
      "partial round trip one way" -> forAll( (s: S) => laws.partialRoundTripOneWay(s)),
      "round trip other way" -> forAll( (a: A) => laws.roundTripOtherWay(a)),
      "modify id = id"       -> forAll( (s: S) => laws.modifyIdentity(s)),
      "modifyF Id = Id"      -> forAll( (s: S) => laws.modifyFId(s)),
      "modifyOption"         -> forAll( (s: S) => laws.modifyOptionIdentity(s)),
      "compose modify"       -> forAll( (s: S, f: A => A, g: A => A) => laws.composeModify(s, f, g)),
      "consistent modify"    -> forAll( (s: S, a: A) => laws.consistentModify(s, a))
    )
  }

}
