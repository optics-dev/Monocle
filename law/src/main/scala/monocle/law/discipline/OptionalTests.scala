package monocle.law.discipline

import monocle.Optional
import monocle.law.OptionalLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

import scalaz.Equal
import scalaz.std.option._

object OptionalTests extends Laws {

  def apply[S: Arbitrary : Equal, A: Arbitrary : Equal](optional: Optional[S, A]): RuleSet = {
    val laws: OptionalLaws[S, A] = new OptionalLaws(optional)
    new SimpleRuleSet("Optional",
      "set what you get" -> forAll( (s: S) => laws.getOptionSet(s)),
      "get what you set" -> forAll( (s: S, a: A) => laws.setGetOption(s, a)),
      "set idempotent"   -> forAll( (s: S, a: A) => laws.setIdempotent(s, a)),
      "modify id = id"   -> forAll( (s: S) => laws.modifyIdentity(s)),
      "modifyF Id = Id"  -> forAll( (s: S) => laws.modifyFId(s)),
      "modifyOption"     -> forAll( (s: S) => laws.modifyOptionIdentity(s))
    )
  }

}
