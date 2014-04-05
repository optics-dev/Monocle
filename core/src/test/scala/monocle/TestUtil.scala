package monocle

import scalaz.Equal


object TestUtil {

  implicit val booleanEqual = Equal.equalA[Boolean]
  implicit val byteEqual    = Equal.equalA[Byte]
  implicit val charEqual    = Equal.equalA[Char]
  implicit val intEqual     = Equal.equalA[Int]
  implicit val longEqual    = Equal.equalA[Long]
  implicit val stringEqual  = Equal.equalA[String]

  implicit def optEq[A: Equal] = scalaz.std.option.optionEqual[A]

  implicit def listEq[A: Equal] = scalaz.std.list.listEqual[A]

  implicit def pairEq[A: Equal, B: Equal] = scalaz.std.tuple.tuple2Equal[A, B]
  implicit def tripleEq[A: Equal, B: Equal, C: Equal] = scalaz.std.tuple.tuple3Equal[A, B, C]

}
