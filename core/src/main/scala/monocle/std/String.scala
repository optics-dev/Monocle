package monocle.std

import monocle.{SimplePrism, SimpleIso}
import SimplePrism.trySimplePrism
import scalaz.syntax.traverse._
import scalaz.std.list._
import scalaz.std.option._
import monocle.std.anyval._

object string extends StringInstances with StringFunctions

trait StringInstances {
  def stringToBoolean = trySimplePrism[String, Boolean](_.toString, _.toBoolean)
  def stringToLong = SimplePrism[String, Long](_.toString, string.parseLong)
  def stringToInt = stringToLong.compose(longToInt)
  def stringToByte = stringToLong.compose(longToByte)

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
