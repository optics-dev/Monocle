package monocle.law.discipline

import monocle._
import monocle.law.OptionalLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

import scalaz.Equal
import scalaz.std.option._

object OptionalTests extends Laws {

  def apply[S: Arbitrary : Equal, A: Arbitrary : Equal](optional: Optional[S, A]): RuleSet =
    apply[S, A, Unit](_ => optional)

  def apply[S: Arbitrary : Equal, A: Arbitrary : Equal, I: Arbitrary](f: I => Optional[S, A]): RuleSet =
    new SimpleRuleSet("Optional",
      "set what you get" -> forAll( (s: S, i: I) => new OptionalLaws(f(i)).getOptionSet(s)),
      "get what you set" -> forAll( (s: S, a: A, i: I) => new OptionalLaws(f(i)).setGetOption(s, a)),
      "set idempotent"   -> forAll( (s: S, a: A, i: I) => new OptionalLaws(f(i)).setIdempotent(s, a)),
      "modify id = id"   -> forAll( (s: S, i: I) => new OptionalLaws(f(i)).modifyIdentity(s)),
      "modifyF Id = Id"  -> forAll( (s: S, i: I) => new OptionalLaws(f(i)).modifyFId(s)),
      "modifyOption"     -> forAll( (s: S, i: I) => new OptionalLaws(f(i)).modifyOptionIdentity(s))
    )

}
