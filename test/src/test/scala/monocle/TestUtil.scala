package monocle

import scalaz._
import org.scalacheck.{Gen, Arbitrary}
import org.scalacheck.Arbitrary._
import scala.Some

object TestUtil {

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
  implicit def mapEq[K: Order, V: Equal] = scalaz.std.map.mapEqual[K, V]

  implicit def tuple2Eq[A1: Equal, A2: Equal] = scalaz.std.tuple.tuple2Equal[A1, A2]
  implicit def tuple3Eq[A1: Equal, A2: Equal, A3: Equal] = scalaz.std.tuple.tuple3Equal[A1, A2, A3]
  implicit def tuple4Eq[A1: Equal, A2: Equal, A3: Equal, A4: Equal] = scalaz.std.tuple.tuple4Equal[A1, A2, A3, A4]
  implicit def tuple5Eq[A1: Equal, A2: Equal, A3: Equal, A4: Equal, A5: Equal] = scalaz.std.tuple.tuple5Equal[A1, A2, A3, A4, A5]
  implicit def tuple6Eq[A1: Equal, A2: Equal, A3: Equal, A4: Equal, A5: Equal, A6: Equal] = scalaz.std.tuple.tuple6Equal[A1, A2, A3, A4, A5, A6]

  // Order instances

  implicit val intOrder = Order.fromScalaOrdering[Int]

  // Show instances

  implicit val intShow = Show.showA[Int]

  implicit def treeShow[A: Show] = new Show[Tree[A]] {
    override def shows(f: Tree[A]): String = f.drawTree
  }

  implicit def streamShow[A: Show] = scalaz.std.stream.streamShow[A]

  // Arbitrary instances

  implicit def treeArb[A](implicit a: Arbitrary[A]): Arbitrary[Tree[A]] =
    Arbitrary {
      val genLeaf = for(label <- Arbitrary.arbitrary[A]) yield Tree.leaf(label)

      def genInternal(sz: Int): Gen[Tree[A]] = for {
        label    <- Arbitrary.arbitrary[A]
        n        <- Gen.choose(sz/3, sz/2)
        children <- Gen.listOfN(n, sizedTree(sz/2))
      } yield Tree.node(label, children.toStream)

      def sizedTree(sz: Int) =
        if(sz <= 0) genLeaf
        else Gen.frequency((1, genLeaf), (3, genInternal(sz)))

      Gen.sized(sz => sizedTree(sz))
    }

  implicit def optionArbitrary[A: Arbitrary]: Arbitrary[Option[A]] = Arbitrary(Gen.frequency(
    1 -> None,
    3 -> Arbitrary.arbitrary[A].map(Option(_))
  ))

  implicit def someArbitrary[A: Arbitrary]: Arbitrary[Some[A]] = Arbitrary(Arbitrary.arbitrary[A].map(Some(_)))

  implicit def arbitraryEither[A: Arbitrary, B: Arbitrary]: Arbitrary[A \/ B] =
    Arbitrary(arbitrary[Either[A, B]] map \/.fromEither)

  implicit def oneAndArb[T[_], A](implicit a: Arbitrary[A], ta: Arbitrary[T[A]]): Arbitrary[OneAnd[T, A]] = Arbitrary(for {
    head <- Arbitrary.arbitrary[A]
    tail <- Arbitrary.arbitrary[T[A]]
  } yield OneAnd(head, tail))

  implicit def vectorArbitrary[A: Arbitrary]: Arbitrary[Vector[A]] =
    Arbitrary(Arbitrary.arbitrary[List[A]].map(_.toVector))

  implicit def iListArbitrary[A: Arbitrary]: Arbitrary[IList[A]] =
    Arbitrary(Arbitrary.arbitrary[List[A]].map(IList.fromList))

}
