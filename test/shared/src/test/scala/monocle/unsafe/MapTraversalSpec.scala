package monocle.unsafe

import monocle.{Iso, MonocleSuite, Traversal}
import monocle.law.{IsoLaws, TraversalLaws}
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws
import cats.Eq
import monocle.unsafe.MapTraversal.{allKeyValues, mapKVTraversal}
import monocle.law.discipline._

object MapListIsoTests extends Laws {
  def apply[S: Arbitrary: Eq, A: Arbitrary: Eq](iso: Iso[S, A])(implicit arbAA: Arbitrary[A => A]): RuleSet = {
    val laws = new IsoLaws(iso)
    new SimpleRuleSet(
      "MapListIso",
      "round trip one way" -> forAll((s: S) => laws.roundTripOneWay(s)),
      // "round trip other way does not work because of key collision and unorderness"
      "modify id = id" -> forAll((s: S) => laws.modifyIdentity(s)),
      // "compose modify does not work because of key collision and unorderness"
      "consistent set with modify"      -> forAll((s: S, a: A) => laws.consistentSetModify(s, a)),
      "consistent modify with modifyId" -> forAll((s: S, f: A => A) => laws.consistentModifyModifyId(s, f)),
      "consistent get with modifyId"    -> forAll((s: S) => laws.consistentGetModifyId(s))
    )
  }
}

object MapKVTraversalTests extends Laws {
  def apply[S: Arbitrary: Eq, A: Arbitrary: Eq](
    traversal: Traversal[S, A]
  )(implicit arbAA: Arbitrary[A => A]): RuleSet =
    apply[S, A, Unit](_ => traversal)

  def apply[S: Arbitrary: Eq, A: Arbitrary: Eq, I: Arbitrary](
    f: I => Traversal[S, A]
  )(implicit arbAA: Arbitrary[A => A]): RuleSet = {
    def laws(i: I): TraversalLaws[S, A] = new TraversalLaws(f(i))
    new SimpleRuleSet(
      "MapKVTraversal",
      "headOption" -> forAll((s: S, i: I) => laws(i).headOption(s)),
      // "get what you set does not work because of key collision unorderness"
      "set idempotent" -> forAll((s: S, a: A, i: I) => laws(i).setIdempotent(s, a)),
      "modify id = id" -> forAll((s: S, i: I) => laws(i).modifyIdentity(s))
      // "compose modify does not work because of key collision and unorderness"
    )
  }
}

class MapTraversalSpec extends MonocleSuite {
  checkAll("map list Iso", MapListIsoTests(allKeyValues[String, Int]))
  checkAll("map KV traversal", MapKVTraversalTests(mapKVTraversal[String, Int]))
}
