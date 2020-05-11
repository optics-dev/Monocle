package monocle

import java.net.URI
import java.util.UUID

import org.scalacheck.Arbitrary._
import org.scalacheck.rng.Seed
import org.scalacheck.{Arbitrary, Cogen, Gen}
import org.scalactic.Equality

import cats._
import cats.free.Cofree
import cats.syntax.eq._

import scala.collection.immutable.ListMap

trait TestInstances
    extends PlatformSpecificTestInstances
    with ScalaVersionSpecificTestInstances
    with cats.instances.AllInstances {
  implicit def equality[A](implicit A: Eq[A]): Equality[A] =
    (a: A, b: Any) => A.eqv(a, b.asInstanceOf[A])

  implicit val genApplicative: Applicative[Gen] = new Applicative[Gen] {
    override def ap[A, B](f: Gen[A => B])(fa: Gen[A]): Gen[B] = fa.flatMap(a => f.map(_(a)))
    override def pure[A](a: A): Gen[A]                        = Gen.const(a)
  }

  // Equal instances
  implicit val uriEqual = Eq.fromUniversalEquals[URI]

  implicit val aritiesEq  = Eq.fromUniversalEquals[Arities]
  implicit val nullaryEq  = Eq.fromUniversalEquals[Nullary]
  implicit val unaryEq    = Eq.fromUniversalEquals[Unary]
  implicit val binaryEq   = Eq.fromUniversalEquals[Binary]
  implicit val quintaryEq = Eq.fromUniversalEquals[Quintary]

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

  implicit def someArbitrary[A: Arbitrary]: Arbitrary[Some[A]] = Arbitrary(Arbitrary.arbitrary[A].map(Some(_)))

  implicit def vectorArbitrary[A: Arbitrary]: Arbitrary[Vector[A]] =
    Arbitrary(Arbitrary.arbitrary[List[A]].map(_.toVector))

  implicit def listMapArbitrary[K: Arbitrary, V: Arbitrary] =
    Arbitrary(Arbitrary.arbitrary[List[(K, V)]].map(l => ListMap(l: _*)))

  implicit def mapArbitrary[K: Arbitrary, V: Arbitrary] =
    Arbitrary(Arbitrary.arbitrary[List[(K, V)]].map(_.toMap))

  implicit def setArbitrary[A: Arbitrary]: Arbitrary[Set[A]] =
    Arbitrary(Arbitrary.arbitrary[List[A]].map(_.toSet))

  implicit def cogenOptionCofree[A](implicit A: Cogen[A]): Cogen[Cofree[Option, A]] =
    Cogen[Cofree[Option, A]]((seed: Seed, t: Cofree[Option, A]) =>
      Cogen[(A, Option[Cofree[Option, A]])].perturb(seed, (t.head, t.tail.value))
    )

  implicit def uuidArbitrary: Arbitrary[UUID] = Arbitrary(UUID.randomUUID)

  implicit def uuidCoGen: Cogen[UUID] =
    Cogen[(Long, Long)].contramap[UUID]((u: UUID) => (u.getMostSignificantBits, u.getLeastSignificantBits))

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
  implicit val binaryGen: Arbitrary[Binary]   = Arbitrary(arbitrary[(String, Int)].map((Binary.apply _) tupled))
  implicit val quintaryGen: Arbitrary[Quintary] = Arbitrary(
    arbitrary[(Char, Boolean, String, Int, Double)].map((Quintary.apply _) tupled)
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
