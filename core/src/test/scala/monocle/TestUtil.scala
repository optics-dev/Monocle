package monocle

import scalaz.Equal


object TestUtil {

  implicit val booleanEqual = Equal.equalA[Boolean]
  implicit val byteEqual    = Equal.equalA[Byte]
  implicit val charEqual    = Equal.equalA[Char]
  implicit val intEqual     = Equal.equalA[Int]
  implicit val longEqual    = Equal.equalA[Long]
  implicit val stringEqual  = Equal.equalA[String]

  implicit def optEq[A: Equal] = new Equal[Option[A]] {
    override def equal(opt1: Option[A], opt2: Option[A]): Boolean = (opt1, opt2) match {
      case (None, None)         => true
      case (Some(a1), Some(a2)) => Equal[A].equal(a1, a2)
      case _                    => false
    }
  }



}
