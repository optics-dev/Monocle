package monocle

import java.net.URI
import java.util.UUID

import org.scalacheck.Arbitrary._
import org.scalacheck.rng.Seed
import org.scalacheck.{Arbitrary, Cogen, Gen}
import org.scalactic.Equality

import cats.data.{Ior => \&/, NonEmptyList, NonEmptyVector, OneAnd, Validated => Validation}
import cats.data.Ior.{Both, Right => That, Left => This}
import cats.{Eq => Equal, _}
import cats.free.Cofree
import cats.syntax.either._
import cats.syntax.eq._
import scala.{Either => \/, List => IList, Vector => IVector}

trait TestInstances extends PlatformSpecificTestInstances with cats.instances.AllInstances {

  implicit def equality[A](implicit A: Equal[A]): Equality[A] =
    new Equality[A]{
      override def areEqual(a: A, b: Any): Boolean =
        A.eqv(a, b.asInstanceOf[A])
    }

  implicit val genApplicative: Applicative[Gen] = new Applicative[Gen] {
    override def ap[A, B](f: Gen[A => B])(fa: Gen[A]): Gen[B] = fa.flatMap(a => f.map(_(a)))
    override def pure[A](a: A): Gen[A] = Gen.const(a)
  }

  // Equal instances
  implicit val uriEqual        = Equal.fromUniversalEquals[URI]

  implicit val aritiesEq       = Equal.fromUniversalEquals[Arities]
  implicit val nullaryEq       = Equal.fromUniversalEquals[Nullary]
  implicit val unaryEq         = Equal.fromUniversalEquals[Unary]
  implicit val binaryEq        = Equal.fromUniversalEquals[Binary]
  implicit val quintaryEq      = Equal.fromUniversalEquals[Quintary]

  implicit def optionCofreeEq[A](implicit A: Equal[A]): Equal[Cofree[Option, A]] =
    Equal.instance { (a, b) =>  A.eqv(a.head, b.head) && a.tail === b.tail }

  implicit def streamCofreeEq[A](implicit A: Equal[A]): Equal[Cofree[Stream, A]] =
    Equal.instance { (a, b) =>  A.eqv(a.head, b.head) && a.tail === b.tail }

  implicit def function1Eq[A, B](implicit A: Arbitrary[A], B: Equal[B]) = new Equal[A => B] {
    val samples = Stream.continually(A.arbitrary.sample).flatten
    val samplesCount = 50

    override def eqv(f: A => B, g: A => B) =
      samples.take(samplesCount).forall { a => B.eqv(f(a), g(a)) }
  }

  implicit def pisoEq[S, T, A, B](implicit StoA: Equal[S => A], BtoT: Equal[B => T]): Equal[PIso[S, T, A, B]] =
    Equal.instance { (a, b) => StoA.eqv(a.get, b.get) && BtoT.eqv(a.reverseGet, b.reverseGet) }

  implicit def pprismEq[S, T, A, B](implicit StoOptA: Equal[S => Option[A]], BtoT: Equal[B => T]): Equal[PPrism[S, T, A, B]] =
    Equal.instance { (a, b) => StoOptA.eqv(a.getOption, b.getOption) && BtoT.eqv(a.reverseGet, b.reverseGet) }

  // Arbitrary instances

  implicit def streamCoGen[A: Cogen]: Cogen[Stream[A]] = Cogen[List[A]].contramap[Stream[A]](_.toList)

  implicit def optionArbitrary[A: Arbitrary]: Arbitrary[Option[A]] = Arbitrary(Gen.frequency(
    1 -> None,
    3 -> Arbitrary.arbitrary[A].map(Option(_))
  ))

  implicit def someArbitrary[A: Arbitrary]: Arbitrary[Some[A]] = Arbitrary(Arbitrary.arbitrary[A].map(Some(_)))

  implicit def validationArbitrary[A: Arbitrary, B: Arbitrary]: Arbitrary[Validation[A, B]] =
    Arbitrary(arbitrary[A \/ B].map(_.toValidated))

  implicit def coGenValidation[E: Cogen, A: Cogen]: Cogen[Validation[E, A]] =
    Cogen.cogenEither[E, A].contramap[Validation[E, A]](_.toEither)

  implicit def theseArbitrary[A: Arbitrary, B: Arbitrary]: Arbitrary[A \&/ B] =
    Arbitrary(Gen.oneOf(
      arbitrary[A].map(This(_)),
      arbitrary[B].map(That(_)),
      for {
        a <- arbitrary[A]
        b <- arbitrary[B]
      } yield Both(a, b)))

  implicit def oneAndArbitrary[T[_], A](implicit a: Arbitrary[A], ta: Arbitrary[T[A]]): Arbitrary[OneAnd[T, A]] = Arbitrary(for {
    head <- Arbitrary.arbitrary[A]
    tail <- Arbitrary.arbitrary[T[A]]
  } yield OneAnd(head, tail))

  implicit def oneAndCoGen[T[_], A](implicit a: Cogen[A], ta: Cogen[T[A]]): Cogen[OneAnd[T, A]] =
    Cogen[(A, T[A])].contramap[OneAnd[T, A]](o => (o.head, o.tail))

  implicit def vectorArbitrary[A: Arbitrary]: Arbitrary[Vector[A]] =
    Arbitrary(Arbitrary.arbitrary[List[A]].map(_.toVector))

  implicit def mapArbitrary[K: Arbitrary, V: Arbitrary] =
    Arbitrary(Arbitrary.arbitrary[List[(K,V)]].map(_.toMap))

  implicit def setArbitrary[A: Arbitrary]: Arbitrary[Set[A]] =
    Arbitrary(Arbitrary.arbitrary[List[A]].map(_.toSet))

  implicit def nelArbitrary[A: Arbitrary]: Arbitrary[NonEmptyList[A]] =
    Arbitrary(oneAndArbitrary[List,A].arbitrary.map( o => NonEmptyList(o.head, o.tail)))

  implicit def nevArbitrary[A: Arbitrary]: Arbitrary[NonEmptyVector[A]] =
    Arbitrary(oneAndArbitrary[Vector,A].arbitrary.map( o => NonEmptyVector(o.head, o.tail)))

  implicit def nelCoGen[A: Cogen]: Cogen[NonEmptyList[A]] =
    Cogen[(A, IList[A])].contramap[NonEmptyList[A]](nel => (nel.head, nel.tail))

  implicit def nevCoGen[A: Cogen]: Cogen[NonEmptyVector[A]] =
    Cogen[(A, IVector[A])].contramap[NonEmptyVector[A]](nev => (nev.head, nev.tail))

  implicit def optionCofreeArbitrary[A](implicit A: Arbitrary[A]): Arbitrary[Cofree[Option, A]] =
    Arbitrary(Arbitrary.arbitrary[OneAnd[List, A]].map( xs =>
      monocle.std.cofree.cofreeToStream.reverseGet(xs.copy(tail = xs.tail.toStream))
    ))

  implicit def cogenOptionCofree[A](implicit A: Cogen[A]): Cogen[Cofree[Option, A]] =
    Cogen[Cofree[Option, A]]((seed: Seed, t: Cofree[Option, A]) => Cogen[(A, Option[Cofree[Option, A]])].perturb(seed, (t.head, t.tail.value)))

  implicit def cogenStreamCofree[A](implicit A: Cogen[A]): Cogen[Cofree[Stream, A]] =
    Cogen[Cofree[Stream, A]]((seed: Seed, t: Cofree[Stream, A]) => Cogen[(A, Stream[Cofree[Stream, A]])].perturb(seed, (t.head, t.tail.value)))

  implicit def uuidArbitrary: Arbitrary[UUID] = Arbitrary(UUID.randomUUID)

  implicit def uuidCoGen: Cogen[UUID] =
    Cogen[(Long, Long)].contramap[UUID]((u: UUID) => (u.getMostSignificantBits, u.getLeastSignificantBits))

  implicit def uriArbitrary: Arbitrary[URI] = Arbitrary {
    val idGen = Gen.nonEmptyListOf(Gen.alphaChar).map(_.mkString)
    for {
      scheme <- idGen
      ssp <- idGen
      fragment <- Gen.option(idGen)
    } yield new URI(scheme, ssp, fragment.orNull)
  }

  implicit def uriCoGen: Cogen[URI] =
    Cogen[String].contramap[URI](_.toString)

  implicit val nullaryGen: Arbitrary[Nullary] = Arbitrary(Gen.const(Nullary()))
  implicit val unaryGen: Arbitrary[Unary] = Arbitrary(arbitrary[Int].map(Unary.apply))
  implicit val binaryGen: Arbitrary[Binary] = Arbitrary(arbitrary[(String, Int)].map((Binary.apply _) tupled))
  implicit val quintaryGen: Arbitrary[Quintary] = Arbitrary(arbitrary[(Char, Boolean, String, Int, Double)].map((Quintary.apply _) tupled))
  implicit val aritiesGen: Arbitrary[Arities] =
    Arbitrary(Gen.oneOf(
      nullaryGen.arbitrary,
      unaryGen.arbitrary,
      binaryGen.arbitrary,
      quintaryGen.arbitrary
    ))
}
