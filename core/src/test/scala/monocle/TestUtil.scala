package monocle

import scalaz.{Tree, Show, Order, Equal}


object TestUtil {

  // Equal instances

  implicit val booleanEqual = Equal.equalA[Boolean]
  implicit val byteEqual    = Equal.equalA[Byte]
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

  implicit val showInt = Show.showA[Int]

  implicit def showTree[A : Show] = new Show[Tree[A]] {
    override def shows(f: Tree[A]): String = f.drawTree
  }
}
