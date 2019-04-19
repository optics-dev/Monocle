package monocle

import java.net.URI
import java.util.UUID

import org.scalacheck.Arbitrary._
import org.scalacheck.rng.Seed
import org.scalacheck.{Arbitrary, Cogen, Gen}
import org.scalactic.Equality

import scalaz.Tree.Node
import scalaz.\&/.{Both, That, This}
import scalaz._
import scalaz.std.list._
import scalaz.syntax.traverse._
import scalaz.syntax.equal._

trait TestInstances extends PlatformSpecificTestInstances {

  implicit def equality[A](implicit A: Equal[A]): Equality[A] =
    (a: A, b: Any) => A.equal(a, b.asInstanceOf[A])

  implicit val genApplicative: Applicative[Gen] = new Applicative[Gen] {
    override def ap[A, B](fa: => Gen[A])(f: => Gen[A => B]): Gen[B] = fa.flatMap(a => f.map(_(a)))
    override def point[A](a: => A): Gen[A] = Gen.const(a)
  }

  // Equal instances
  implicit val booleanEqual    = Equal.equalA[Boolean]
  implicit val byteEqual       = Equal.equalA[Byte]
  implicit val shortEqual      = Equal.equalA[Short]
  implicit val charEqual       = Equal.equalA[Char]
  implicit val intEqual        = Equal.equalA[Int]
  implicit val longEqual       = Equal.equalA[Long]
  implicit val floatEqual      = Equal.equalA[Float]
  implicit val doubleEqual     = Equal.equalA[Double]
  implicit val stringEqual     = Equal.equalA[String]
  implicit val unitEqual       = Equal.equalA[Unit]
  implicit val bigIntEqual     = Equal.equalA[BigInt]
  implicit val bigDecimalEqual = Equal.equalA[BigDecimal]
  implicit val uuidEqual       = Equal.equalA[UUID]
  implicit val uriEqual        = Equal.equalA[URI]

  implicit val aritiesEq       = Equal.equalA[Arities]
  implicit val nullaryEq       = Equal.equalA[Nullary]
  implicit val unaryEq         = Equal.equalA[Unary]
  implicit val binaryEq        = Equal.equalA[Binary]
  implicit val quintaryEq      = Equal.equalA[Quintary]

  implicit def optEq[A: Equal] = scalaz.std.option.optionEqual[A]
  implicit def someEq[A: Equal] = Equal.equalA[Some[A]]
  implicit def eitherEq[A: Equal, B: Equal] = scalaz.std.either.eitherEqual[A, B]
  implicit def listEq[A: Equal] = scalaz.std.list.listEqual[A]
  implicit def vectorEq[A: Equal] = scalaz.std.vector.vectorEqual[A]
  implicit def streamEq[A: Equal] = scalaz.std.stream.streamEqual[A]
  implicit def setEq[A: Order] = scalaz.std.set.setOrder[A]
  implicit def mapEq[K: Order, V: Equal] = scalaz.std.map.mapEqual[K, V]

  implicit def tuple2Eq[A1: Equal, A2: Equal] = scalaz.std.tuple.tuple2Equal[A1, A2]
  implicit def tuple3Eq[A1: Equal, A2: Equal, A3: Equal] = scalaz.std.tuple.tuple3Equal[A1, A2, A3]
  implicit def tuple4Eq[A1: Equal, A2: Equal, A3: Equal, A4: Equal] = scalaz.std.tuple.tuple4Equal[A1, A2, A3, A4]
  implicit def tuple5Eq[A1: Equal, A2: Equal, A3: Equal, A4: Equal, A5: Equal] = scalaz.std.tuple.tuple5Equal[A1, A2, A3, A4, A5]
  implicit def tuple6Eq[A1: Equal, A2: Equal, A3: Equal, A4: Equal, A5: Equal, A6: Equal] = scalaz.std.tuple.tuple6Equal[A1, A2, A3, A4, A5, A6]

  implicit def optionCofreeEq[A](implicit A: Equal[A]): Equal[Cofree[Option, A]] =
    Equal.equal { (a, b) =>  A.equal(a.head, b.head) && a.tail === b.tail }

  implicit def streamCofreeEq[A](implicit A: Equal[A]): Equal[Cofree[Stream, A]] =
    Equal.equal { (a, b) =>  A.equal(a.head, b.head) && a.tail === b.tail }

  implicit def function1Eq[A, B](implicit A: Arbitrary[A], B: Equal[B]) = new Equal[A => B] {
    val samples = Stream.continually(A.arbitrary.sample).flatten
    val samplesCount = 50

    override def equal(f: A => B, g: A => B) =
      samples.take(samplesCount).forall { a => B.equal(f(a), g(a)) }
  }

  implicit def pisoEq[S, T, A, B](implicit StoA: Equal[S => A], BtoT: Equal[B => T]): Equal[PIso[S, T, A, B]] =
    Equal.equal { (a, b) => StoA.equal(a.get, b.get) && BtoT.equal(a.reverseGet, b.reverseGet) }

  implicit def pprismEq[S, T, A, B](implicit StoOptA: Equal[S => Option[A]], BtoT: Equal[B => T]): Equal[PPrism[S, T, A, B]] =
    Equal.equal { (a, b) => StoOptA.equal(a.getOption, b.getOption) && BtoT.equal(a.reverseGet, b.reverseGet) }

  // Order instances

  implicit val intOrder = Order.fromScalaOrdering[Int]

  // Show instances

  implicit val intShow = Show.showA[Int]

  implicit def treeShow[A: Show] = new Show[Tree[A]] {
    override def shows(f: Tree[A]): String = f.drawTree
  }

  implicit def streamShow[A: Show] = scalaz.std.stream.streamShow[A]

  // Arbitrary instances

  implicit def treeArbitrary[A: Arbitrary]: Arbitrary[Tree[A]] =
    Arbitrary {
      def genPartition(sum: Int): Gen[List[Int]] =
        if(sum <= 0) Gen.const(Nil)
        else for {
          n    <- Gen.choose(1, sum)
          tail <- genPartition(sum - n)
        } yield n :: tail

      def sizedTree(size: Int): Gen[Tree[A]] =
        for {
          value      <- Arbitrary.arbitrary[A]
          partitions <- genPartition(size - 1)
          children   <- partitions.traverseU(sizedTree)
        } yield Node[A](value, children.toStream)

      Gen.sized(sz => sizedTree(sz))
    }

  implicit def treeCoGen[A: Cogen]: Cogen[Tree[A]] =
    Cogen[Tree[A]]((seed: Seed, t: Tree[A]) => Cogen[(A, Stream[Tree[A]])].perturb(seed, (t.rootLabel, t.subForest)))

  implicit def streamCoGen[A: Cogen]: Cogen[Stream[A]] = Cogen[List[A]].contramap[Stream[A]](_.toList)

  implicit def optionArbitrary[A: Arbitrary]: Arbitrary[Option[A]] = Arbitrary(Gen.frequency(
    1 -> None,
    3 -> Arbitrary.arbitrary[A].map(Option(_))
  ))

  implicit def maybeArbitrary[A: Arbitrary]: Arbitrary[Maybe[A]] = Arbitrary(Gen.frequency(
    1 -> Maybe.empty[A],
    3 -> Arbitrary.arbitrary[A].map(Maybe.just(_))
  ))

  implicit def iListCoGen[A: Cogen]: Cogen[IList[A]] = Cogen[List[A]].contramap[IList[A]](_.toList)

  implicit def someArbitrary[A: Arbitrary]: Arbitrary[Some[A]] = Arbitrary(Arbitrary.arbitrary[A].map(Some(_)))

  implicit def disjunctionArbitrary[A: Arbitrary, B: Arbitrary]: Arbitrary[A \/ B] =
    Arbitrary(arbitrary[Either[A, B]] map \/.fromEither)

  implicit def coGenDisjunction[E: Cogen, A: Cogen]: Cogen[E \/ A] =
    Cogen.cogenEither[E, A].contramap[E \/ A](_.toEither)

  implicit def validationArbitrary[A: Arbitrary, B: Arbitrary]: Arbitrary[Validation[A, B]] =
    Arbitrary(arbitrary[A \/ B].map(_.validation))

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

  implicit def iListArbitrary[A: Arbitrary]: Arbitrary[IList[A]] =
    Arbitrary(Arbitrary.arbitrary[List[A]].map(IList.fromList))

  implicit def mapArbitrary[K: Arbitrary, V: Arbitrary] =
    Arbitrary(Arbitrary.arbitrary[List[(K,V)]].map(_.toMap))

  implicit def iMapArbitrary[K: Arbitrary: Order, V: Arbitrary] =
    Arbitrary(Arbitrary.arbitrary[List[(K,V)]].map(l => ==>>.fromList(l)(Order[K])))

  implicit def setArbitrary[A: Arbitrary]: Arbitrary[Set[A]] =
    Arbitrary(Arbitrary.arbitrary[List[A]].map(_.toSet))

  implicit def iSetArbitrary[A: Arbitrary: Order]: Arbitrary[ISet[A]] =
    Arbitrary(Arbitrary.arbitrary[List[A]].map(l => ISet.fromList(l)(Order[A])))

  implicit def nelArbitrary[A: Arbitrary]: Arbitrary[NonEmptyList[A]] =
    Arbitrary(oneAndArbitrary[List,A].arbitrary.map( o => NonEmptyList(o.head, o.tail:_*)))

  implicit def nelCoGen[A: Cogen]: Cogen[NonEmptyList[A]] =
    Cogen[(A, IList[A])].contramap[NonEmptyList[A]](nel => (nel.head, nel.tail))

  implicit def either3Arbitrary[A: Arbitrary, B: Arbitrary, C: Arbitrary]: Arbitrary[Either3[A, B, C]] =
    Arbitrary(Gen.oneOf(
      Arbitrary.arbitrary[A].map(Either3.left3),
      Arbitrary.arbitrary[B].map(Either3.middle3),
      Arbitrary.arbitrary[C].map(Either3.right3)
    ))

  implicit def optionCofreeArbitrary[A](implicit A: Arbitrary[A]): Arbitrary[Cofree[Option, A]] =
    Arbitrary(Arbitrary.arbitrary[OneAnd[List, A]].map( xs =>
      monocle.std.cofree.cofreeToStream.reverseGet(xs.copy(tail = xs.tail.toStream))
    ))

  implicit def streamCofreeArbitrary[A](implicit A: Arbitrary[A]): Arbitrary[Cofree[Stream, A]] =
    Arbitrary(Arbitrary.arbitrary[Tree[A]].map( monocle.std.cofree.cofreeToTree.reverseGet))

  implicit def cogenOptionCofree[A](implicit A: Cogen[A]): Cogen[Cofree[Option, A]] =
    Cogen[Cofree[Option, A]]((seed: Seed, t: Cofree[Option, A]) => Cogen[(A, Option[Cofree[Option, A]])].perturb(seed, (t.head, t.tail)))

  implicit def cogenStreamCofree[A](implicit A: Cogen[A]): Cogen[Cofree[Stream, A]] =
    Cogen[Cofree[Stream, A]]((seed: Seed, t: Cofree[Stream, A]) => Cogen[(A, Stream[Cofree[Stream, A]])].perturb(seed, (t.head, t.tail)))

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
