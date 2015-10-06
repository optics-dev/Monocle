package monocle

import eu.timepit.refined.Refined
import monocle.date.{Hour, Minute}
import org.joda.time.DateTime
import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.scalactic.Equality

import scalaz.\&/.{Both, That, This}
import scalaz._
import scalaz.std.list._
import scalaz.syntax.traverse._

trait TestInstances {

  implicit def equality[A](implicit A: Equal[A]): Equality[A] =
    new Equality[A]{
      override def areEqual(a: A, b: Any): Boolean =
        A.equal(a, b.asInstanceOf[A])
    }

  implicit val genApplicative: Applicative[Gen] = new Applicative[Gen] {
    override def ap[A, B](fa: => Gen[A])(f: => Gen[A => B]): Gen[B] = fa.flatMap(a => f.map(_(a)))
    override def point[A](a: => A): Gen[A] = Gen.const(a)
  }

  // Equal instances
  implicit val booleanEqual = Equal.equalA[Boolean]
  implicit val byteEqual    = Equal.equalA[Byte]
  implicit val shortEqual   = Equal.equalA[Short]
  implicit val charEqual    = Equal.equalA[Char]
  implicit val intEqual     = Equal.equalA[Int]
  implicit val longEqual    = Equal.equalA[Long]
  implicit val floatEqual   = Equal.equalA[Float]
  implicit val doubleEqual  = Equal.equalA[Double]
  implicit val stringEqual  = Equal.equalA[String]
  implicit val unitEqual    = Equal.equalA[Unit]

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

  implicit val dateTimeEq: Equal[DateTime] = Equal.equalA

  implicit val minuteEq: Equal[Minute] = Equal.equalA
  implicit val hourEq  : Equal[Hour]   = Equal.equalA

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
        } yield Tree.node[A](value, children.toStream)

      Gen.sized(sz => sizedTree(sz))
    }

  implicit def optionArbitrary[A: Arbitrary]: Arbitrary[Option[A]] = Arbitrary(Gen.frequency(
    1 -> None,
    3 -> Arbitrary.arbitrary[A].map(Option(_))
  ))

  implicit def maybeArbitrary[A: Arbitrary]: Arbitrary[Maybe[A]] = Arbitrary(Gen.frequency(
    1 -> Maybe.empty[A],
    3 -> Arbitrary.arbitrary[A].map(Maybe.just(_))
  ))

  implicit def someArbitrary[A: Arbitrary]: Arbitrary[Some[A]] = Arbitrary(Arbitrary.arbitrary[A].map(Some(_)))

  implicit def disjunctionArbitrary[A: Arbitrary, B: Arbitrary]: Arbitrary[A \/ B] =
    Arbitrary(arbitrary[Either[A, B]] map \/.fromEither)

  implicit def validationArbitrary[A: Arbitrary, B: Arbitrary]: Arbitrary[Validation[A, B]] =
    Arbitrary(arbitrary[A \/ B].map(_.validation))

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

  implicit def either3Arbitrary[A: Arbitrary, B: Arbitrary, C: Arbitrary]: Arbitrary[Either3[A, B, C]] =
    Arbitrary(Gen.oneOf(
      Arbitrary.arbitrary[A].map(Either3.left3),
      Arbitrary.arbitrary[B].map(Either3.middle3),
      Arbitrary.arbitrary[C].map(Either3.right3)
    ))

  implicit val dateTimeArbitrary: Arbitrary[DateTime] = Arbitrary(
    Gen.choose(0L, Long.MaxValue).map(new DateTime(_))
  )

  implicit val minuteArbitrary: Arbitrary[Minute] = Arbitrary(
    Gen.choose(0, 59).map(Refined.unsafeApply)
  )

  implicit val hourArbitrary: Arbitrary[Hour] = Arbitrary(
    Gen.choose(0, 23).map(Refined.unsafeApply)
  )

}
