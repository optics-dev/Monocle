package monocle

import org.scalacheck.{Gen, Arbitrary}
import scalaz.{Tree, Show, Order, Equal}


object TestUtil {

  // Equal instances

  implicit val booleanEqual = Equal.equalA[Boolean]
  implicit val byteEqual    = Equal.equalA[Byte]
  implicit val shortEqual   = Equal.equalA[Short]
  implicit val charEqual    = Equal.equalA[Char]
  implicit val intEqual     = Equal.equalA[Int]
  implicit val longEqual    = Equal.equalA[Long]
  implicit val floatEqual   = Equal.equalA[Float]
  implicit val stringEqual  = Equal.equalA[String]

  implicit def optEq[A: Equal] = scalaz.std.option.optionEqual[A]
  implicit def listEq[A: Equal] = scalaz.std.list.listEqual[A]
  implicit def streamEq[A: Equal] = scalaz.std.stream.streamEqual[A]
  implicit def mapEq[K: Order, V: Equal] = scalaz.std.map.mapEqual[K, V]

  implicit def tuple2Eq[A1: Equal, A2: Equal] = scalaz.std.tuple.tuple2Equal[A1, A2]
  implicit def tuple3Eq[A1: Equal, A2: Equal, A3: Equal] = scalaz.std.tuple.tuple3Equal[A1, A2, A3]
  implicit def tuple4Eq[A1: Equal, A2: Equal, A3: Equal, A4: Equal] = scalaz.std.tuple.tuple4Equal[A1, A2, A3, A4]
  implicit def tuple5Eq[A1: Equal, A2: Equal, A3: Equal, A4: Equal, A5: Equal] = scalaz.std.tuple.tuple5Equal[A1, A2, A3, A4, A5]
  implicit def tuple6Eq[A1: Equal, A2: Equal, A3: Equal, A4: Equal, A5: Equal, A6: Equal] = scalaz.std.tuple.tuple6Equal[A1, A2, A3, A4, A5, A6]


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

  implicit def optionArbitrary[A](implicit a: Arbitrary[A]): Arbitrary[Option[A]] = Arbitrary(Gen.frequency(
    1 -> None,
    3 -> Arbitrary.arbitrary[A].map(Option(_))
  ))

}
