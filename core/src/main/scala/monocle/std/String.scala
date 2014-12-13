package monocle.std

import monocle.function._
import monocle.{Iso, Prism}

import scalaz.Maybe
import scalaz.std.list._
import scalaz.syntax.traverse._

object string extends StringInstances

trait StringInstances {

  val stringToList: Iso[String, List[Char]] =
    Iso((_: String).toList)(_.mkString)

  val stringToBoolean: Prism[String, Boolean] =
    Prism{s: String => Maybe.fromTryCatchNonFatal(s.toBoolean)}(_.toString)

  val stringToLong: Prism[String, Long] =
    Prism(parseLong)(_.toString)

  val stringToInt: Prism[String, Int] =
    stringToLong composePrism long.longToInt

  val stringToByte: Prism[String, Byte] =
    stringToLong composePrism long.longToByte


  implicit val stringEmpty: Empty[String] =
    new Empty[String] {
      def empty = Prism[String, Unit](s => if(s.isEmpty) Maybe.just(()) else Maybe.empty)(_ => "")
    }

  implicit val stringReverse: Reverse[String, String] =
    reverseFromReverseFunction[String](_.reverse)

  implicit val stringEach: Each[String, Char] =
    new Each[String, Char] {
      def each =
        stringToList composeTraversal Each.each[List[Char], Char]
    }

  implicit val stringIndex: Index[String, Int, Char] =
    new Index[String, Int, Char]{
      def index(i: Int) =
        stringToList composeOptional Index.index[List[Char], Int, Char](i)
    }

  implicit val stringFilterIndex: FilterIndex[String, Int, Char] =
    new FilterIndex[String, Int, Char]{
      def filterIndex(predicate: Int => Boolean) =
        stringToList composeTraversal FilterIndex.filterIndex[List[Char], Int, Char](predicate)
    }

  implicit val stringCons: Cons[String, Char] =
    new Cons[String, Char] {
      def cons =
        Prism[String, (Char, String)](s =>
          if(s.isEmpty) Maybe.empty else Maybe.just((s.head, s.tail))
        ){ case (h, t) => h + t }
  }

  implicit val stringSnoc: Snoc[String, Char] = new Snoc[String, Char]{
    def snoc =
      Prism[String, (String, Char)](
        s => if(s.isEmpty) Maybe.empty else Maybe.just((s.init, s.last))){
        case (init, last) => init :+ last
      }
  }


  private def parseLong(s: String): Maybe[Long] =
    if (s.isEmpty) Maybe.empty
    else s.toList match {
      case '-' :: xs => parseLongUnsigned(xs).map(-_)
      case xs        => parseLongUnsigned(xs)
      // we reject case where String starts with +, otherwise it will be an invalid Prism according 2nd Prism law
    }

  private def parseLongUnsigned(s: List[Char]): Maybe[Long] =
    s.traverse(charToDigit).map(_.foldl(0L)(n => d => n * 10 + d))

  private def charToDigit(c: Char): Maybe[Int] =
    if (c >= '0' && c <= '9') Maybe.just(c - '0')
    else Maybe.empty

}