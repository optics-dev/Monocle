package monocle

import cats.Eq
import cats.laws.discipline.{FunctorTests, SemigroupalTests}
import org.scalacheck.{Arbitrary, Gen}
import GetterInstancesSpec.eqGetter

//I'm wondering if this is worth keeping with the
//sampledEq being deprecated
class GetterInstancesSpec extends MonocleSuite {
  case class Sample(a: Char, b: Int, c: Boolean)
  implicit val sampleEq: Eq[Sample] = Eq.fromUniversalEquals
  implicit val sampleArb: Arbitrary[Sample] = Arbitrary(
    for {
      a <- Arbitrary.arbChar.arbitrary
      b <- Arbitrary.arbInt.arbitrary
      c <- Arbitrary.arbBool.arbitrary
    } yield Sample(a, b, c)
  )

  implicit val arbSampleLensGetter: Arbitrary[Getter[Sample, Char]] = Arbitrary(
    Gen.const(Getter[Sample, Char](_.a))
  )
  implicit val arbSampleGetterInt: Arbitrary[Getter[Sample, Int]] = Arbitrary(
    Gen.const(Getter[Sample, Int](_.b))
  )
  implicit val arbSampleGetterBool: Arbitrary[Getter[Sample, Boolean]] = Arbitrary(
    Gen.const(Getter[Sample, Boolean](_.c))
  )

  checkAll(
    "Getter.SemigroupalLaws",
    SemigroupalTests[Getter[Sample, *]]
      .semigroupal[Char, Int, Boolean]
  )

  checkAll(
    "Getter.FunctorLaws",
    FunctorTests[Getter[Sample, *]]
      .functor[Char, Int, Boolean]
  )
}

object GetterInstancesSpec {
  implicit def eqGetter[S, A](implicit
    eqA: Eq[A],
    arbS: Arbitrary[S]
  ): Eq[Getter[S, A]] =
    InheritFromCatsLaws.SampledEq.catsLawsSampledEq[Getter[S, A], S, A](arbS, eqA) { case (getter, sample) =>
      getter.get(sample)
    }

  object InheritFromCatsLaws {
    @deprecated(
      "Inherited from cats",
      "This helper method is deprecated from cats, looking for a better idea on running eq for lens"
    )
    class SampledEq {
      import cats.laws.discipline.DeprecatedEqInstances.sampledEq
      def catsLawsSampledEq[A, B, C](implicit B: Arbitrary[B], evEq: Eq[C]): ((A, B) => C) => Eq[A] =
        sampledEq[A, B, C](100)(_)
    }
    object SampledEq extends SampledEq
  }
}
