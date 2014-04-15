package monocle.std

import monocle.SimpleIso
import scalaz.syntax.traverse._
import scalaz.std.list._
import scalaz.std.option._

object string extends StringInstances with StringFunctions

trait StringInstances {
  val stringToList = SimpleIso[String, List[Char]](_.toList, _.mkString)
}

trait StringFunctions {
  def parseLong(s: String): Option[Long] =
    if (s.isEmpty) None
    else s.toList match {
      case '-' :: xs => parseLongUnsigned(xs).map(-_)
      case '+' :: xs => parseLongUnsigned(xs)
      case xs => parseLongUnsigned(xs)
    }

  def parseLongUnsigned(s: List[Char]): Option[Long] = s.traverse(charToDigit).map(_.foldl(0L)(n => d => n * 10 + d))

  def charToDigit(c: Char): Option[Int] =
    if (c >= '0' && c <= '9')
      Some(c - '0')
    else
      None
}
