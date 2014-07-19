package monocle.std

import monocle.SimplePrism._
import monocle.function._
import monocle.{SimplePrism, SimpleIso}
import scalaz.std.option._
import scalaz.std.list._
import scalaz.syntax.traverse._

object string extends StringInstances

trait StringInstances {

  val stringToList = SimpleIso[String, List[Char]](_.toList, _.mkString)

  implicit val stringEach: Each[String, Char] = new Each[String, Char] {
    def each = stringToList composeTraversal Each.each[List[Char], Char]
  }

  implicit val stringIndex: Index[String, Int, Char] = new Index[String, Int, Char]{
    def index(i: Int) = stringToList composeOptional Index.index[List[Char], Int, Char](i)
  }

  implicit val stringFilterIndex: FilterIndex[String, Int, Char] = new FilterIndex[String, Int, Char]{
    def filterIndex(predicate: Int => Boolean) =
      stringToList composeTraversal FilterIndex.filterIndex[List[Char], Int, Char](predicate)
  }

  implicit val stringHeadOption: HeadOption[String, Char] = new HeadOption[String, Char] {
    def headOption = stringToList composeOptional HeadOption.headOption[List[Char], Char]
  }

  implicit val stringTailOption: TailOption[String, String] = new TailOption[String, String]{
    def tailOption = stringToList composeOptional TailOption.tailOption[List[Char], List[Char]] composeOptional stringToList.reverse
  }

  implicit val stringLastOption: LastOption[String, Char] =
    LastOption.reverseHeadLastOption[String   , Char]

  implicit val stringInitOption: InitOption[String, String] =
    InitOption.reverseTailInitOption[String]

  implicit val stringReverse: Reverse[String, String] = Reverse.simple[String](_.reverse)

  implicit val stringToBoolean = new SafeCast[String, Boolean] {
    def safeCast = trySimplePrism[String, Boolean](_.toString, _.toBoolean)
  }

  implicit val stringToLong = new SafeCast[String, Long] {
    def safeCast = SimplePrism[String, Long](_.toString, parseLong)
  }

  implicit val stringToInt = new SafeCast[String, Int] {
    def safeCast = SafeCast.safeCast[String, Long] composePrism SafeCast.safeCast[Long, Int]
  }

  implicit val stringToByte = new SafeCast[String, Byte] {
    def safeCast = SafeCast.safeCast[String, Long] composePrism SafeCast.safeCast[Long, Byte]
  }

  private def parseLong(s: String): Option[Long] =
    if (s.isEmpty) None
    else s.toList match {
      case '-' :: xs => parseLongUnsigned(xs).map(-_)
      case '+' :: xs => parseLongUnsigned(xs)
      case xs => parseLongUnsigned(xs)
    }

  private def parseLongUnsigned(s: List[Char]): Option[Long] =
    s.traverse(charToDigit).map(_.foldl(0L)(n => d => n * 10 + d))

  private def charToDigit(c: Char): Option[Int] =
    if (c >= '0' && c <= '9') Some(c - '0')
    else None

}