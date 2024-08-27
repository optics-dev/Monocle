package monocle

import java.net.URI

import org.scalacheck.Arbitrary._
import org.scalacheck.rng.Seed
import org.scalacheck.{Arbitrary, Cogen, Gen}

import cats._
import cats.free.Cofree
import cats.syntax.eq._

import scala.collection.immutable.ListMap

trait TestInstances extends PlatformSpecificTestInstances with cats.instances.AllInstances {
  implicit val genApplicative: Applicative[Gen] = new Applicative[Gen] {
    override def ap[A, B](f: Gen[A => B])(fa: Gen[A]): Gen[B] =
      fa.flatMap(a => f.map(_(a)))
    override def pure[A](a: A): Gen[A] = Gen.const(a)
  }

  // Equal instances
  implicit val uriEqual: Eq[URI] = Eq.fromUniversalEquals[URI]

  implicit val aritiesEq: Eq[Arities]   = Eq.fromUniversalEquals[Arities]
  implicit val nullaryEq: Eq[Nullary]   = Eq.fromUniversalEquals[Nullary]
  implicit val unaryEq: Eq[Unary]       = Eq.fromUniversalEquals[Unary]
  implicit val binaryEq: Eq[Binary]     = Eq.fromUniversalEquals[Binary]
  implicit val quintaryEq: Eq[Quintary] = Eq.fromUniversalEquals[Quintary]

  implicit def function1Eq[A, B](implicit A: Arbitrary[A], B: Eq[B]): Eq[A => B] =
    new Eq[A => B] {
      val samples      = LazyList.continually(A.arbitrary.sample).flatten
      val samplesCount = 50

      override def eqv(f: A => B, g: A => B) =
        samples.take(samplesCount).forall(a => B.eqv(f(a), g(a)))
    }

  implicit def optionCofreeEq[A](implicit A: Eq[A]): Eq[Cofree[Option, A]] =
    Eq.instance((a, b) => A.eqv(a.head, b.head) && a.tail === b.tail)

  implicit def pisoEq[S, T, A, B](implicit StoA: Eq[S => A], BtoT: Eq[B => T]): Eq[PIso[S, T, A, B]] =
    Eq.instance((a, b) => StoA.eqv(a.get, b.get) && BtoT.eqv(a.reverseGet, b.reverseGet))

  implicit def pprismEq[S, T, A, B](implicit StoOptA: Eq[S => Option[A]], BtoT: Eq[B => T]): Eq[PPrism[S, T, A, B]] =
    Eq.instance((a, b) => StoOptA.eqv(a.getOption, b.getOption) && BtoT.eqv(a.reverseGet, b.reverseGet))

  // Arbitrary instances

  implicit def optionArbitrary[A: Arbitrary]: Arbitrary[Option[A]] =
    Arbitrary(
      Gen.frequency(
        1 -> None,
        3 -> Arbitrary.arbitrary[A].map(Option(_))
      )
    )

  implicit def someArbitrary[A: Arbitrary]: Arbitrary[Some[A]] =
    Arbitrary(Arbitrary.arbitrary[A].map(Some(_)))

  implicit def vectorArbitrary[A: Arbitrary]: Arbitrary[Vector[A]] =
    Arbitrary(Arbitrary.arbitrary[List[A]].map(_.toVector))

  implicit def listMapArbitrary[K: Arbitrary, V: Arbitrary]: Arbitrary[ListMap[K, V]] =
    Arbitrary(Arbitrary.arbitrary[List[(K, V)]].map(l => ListMap(l*)))

  implicit def mapArbitrary[K: Arbitrary, V: Arbitrary]: Arbitrary[Map[K, V]] =
    Arbitrary(Arbitrary.arbitrary[List[(K, V)]].map(_.toMap))

  implicit def setArbitrary[A: Arbitrary]: Arbitrary[Set[A]] =
    Arbitrary(Arbitrary.arbitrary[List[A]].map(_.toSet))

  implicit def cogenOptionCofree[A](implicit A: Cogen[A]): Cogen[Cofree[Option, A]] =
    Cogen[Cofree[Option, A]]((seed: Seed, t: Cofree[Option, A]) =>
      Cogen[(A, Option[Cofree[Option, A]])]
        .perturb(seed, (t.head, t.tail.value))
    )

  implicit def uriArbitrary: Arbitrary[URI] =
    Arbitrary {
      val idGen = Gen.nonEmptyListOf(Gen.alphaChar).map(_.mkString)
      for {
        scheme   <- idGen
        ssp      <- idGen
        fragment <- Gen.option(idGen)
      } yield new URI(scheme, ssp, fragment.orNull)
    }

  implicit def uriCoGen: Cogen[URI] =
    Cogen[String].contramap[URI](_.toString)

  implicit val nullaryGen: Arbitrary[Nullary] = Arbitrary(Gen.const(Nullary()))
  implicit val unaryGen: Arbitrary[Unary]     = Arbitrary(arbitrary[Int].map(Unary.apply))
  implicit val binaryGen: Arbitrary[Binary]   = Arbitrary(arbitrary[(String, Int)].map(Binary.apply.tupled))
  implicit val quintaryGen: Arbitrary[Quintary] = Arbitrary(
    arbitrary[(Char, Boolean, String, Int, Double)]
      .map(Quintary.apply.tupled)
  )
  implicit val aritiesGen: Arbitrary[Arities] =
    Arbitrary(
      Gen.oneOf(
        nullaryGen.arbitrary,
        unaryGen.arbitrary,
        binaryGen.arbitrary,
        quintaryGen.arbitrary
      )
    )
}
