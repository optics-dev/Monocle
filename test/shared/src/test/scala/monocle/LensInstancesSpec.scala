package monocle

import cats.Eq
import cats.implicits.catsSyntaxTuple2Semigroupal
import cats.laws.discipline.{InvariantTests, SemigroupalTests}
import org.scalacheck.{Arbitrary, Gen}

//I'm wondering if this is worth keeping with the
//sampledEq being deprecated
class LensInstancesSpec extends MonocleSuite
{
  case class Sample(a: Char, b: Int, c: Boolean)
  implicit val sampleEq: Eq[Sample] = Eq.fromUniversalEquals
  implicit val sampleArb: Arbitrary[Sample] = Arbitrary(
    for {
      a <- Arbitrary.arbChar.arbitrary
      b <- Arbitrary.arbInt.arbitrary
      c <- Arbitrary.arbBool.arbitrary
    } yield Sample(a, b, c)
  )

  implicit val arbSampleLensChar: Arbitrary[Lens[Sample, Char]] = Arbitrary(
    Gen.const(Lens[Sample, Char](_.a)(c => s => s.copy(a = c)))
  )
  implicit val arbSampleLensInt: Arbitrary[Lens[Sample, Int]] = Arbitrary(
    Gen.const(Lens[Sample, Int](_.b)(i => s => s.copy(b = i)))
  )
  implicit val arbSampleLensBool: Arbitrary[Lens[Sample, Boolean]] = Arbitrary(
    Gen.const(Lens[Sample, Boolean](_.c)(b => s => s.copy(c = b)))
  )

  checkAll("Lens.InvariantLaws", InvariantTests[Lens[Sample, *]].invariant[Char, Int, Boolean])

  checkAll("Lens.SemigroupalLaws", SemigroupalTests[Lens[Sample, *]].semigroupal[Char, Int, Boolean])

  //Explicit tests for clarity
  test("conflicting lenses are left associative") {
    val charLens = Lens[Sample, Char](_.a)(aChar => s => s.copy(a = aChar))

    val sample = Sample('a', 0, false)
    val zippedLenses: Lens[Sample, (Char, Char)] =
      (charLens, charLens).tupled

    assertEquals(zippedLenses.replace('b', 'c')(sample), sample.copy(a = 'c'))
  }
}


object LensInstancesSpec {
  implicit def eqLens[S, A](implicit
                            eqS: Eq[S],
                            eqA: Eq[A],
                            arbS: Arbitrary[S],
                            arbA: Arbitrary[A]
                           ): Eq[Lens[S, A]] = {
    val arbSA = Arbitrary(
      for {
        s <- arbS.arbitrary
        a <- arbA.arbitrary
      } yield (s, a)
    )
    val eqSA = Eq.and(Eq.by[(S,A), S](_._1), Eq.by[(S, A), A](_._2))
    InheritFromCatsLaws.SampledEq.catsLawsSampledEq[Lens[S, A], (S, A), (S, A)](arbSA, eqSA) {
      case (l, (s, a)) =>
        l.replace(a)(s) -> l.get(s)
    }
  }

  object InheritFromCatsLaws {
    @deprecated("Inherited from cats", "This helper method is deprecated from cats, looking for a better idea on running eq for lens")
    class SampledEq {
      import cats.laws.discipline.DeprecatedEqInstances.sampledEq
      def catsLawsSampledEq[A, B, C](implicit B: Arbitrary[B], evEq: Eq[C]): ((A, B) => C) => Eq[A] =
        sampledEq[A, B, C](100)(_)
    }
    object SampledEq extends SampledEq
  }
}