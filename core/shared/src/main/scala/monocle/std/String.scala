package monocle.std

import monocle.function._
import monocle.std.list._
import monocle.{Iso, Prism, Traversal}

import scalaz.Applicative
import scalaz.std.list._
import scalaz.std.option._
import scalaz.syntax.traverse._

object string extends StringOptics

trait StringOptics {

  val stringToList: Iso[String, List[Char]] =
    Iso((_: String).toList)(_.mkString)

  val stringToBoolean: Prism[String, Boolean] =
    Prism{s: String => parseCaseSensitiveBoolean(s)}(_.toString)

  val stringToLong: Prism[String, Long] =
    Prism(parseLong)(_.toString)

  val stringToInt: Prism[String, Int] =
    stringToLong composePrism long.longToInt

  val stringToByte: Prism[String, Byte] =
    stringToLong composePrism long.longToByte


  implicit val stringEmpty: Empty[String] =
    new Empty[String] {
      def empty = Prism[String, Unit](s => if(s.isEmpty) Some(()) else None)(_ => "")
    }

  implicit val stringReverse: Reverse[String, String] =
    Reverse.reverseFromReverseFunction[String](_.reverse)

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
          if(s.isEmpty) None else Some((s.head, s.tail))
        ){ case (h, t) => h + t }
  }

  implicit val stringSnoc: Snoc[String, Char] = new Snoc[String, Char]{
    def snoc =
      Prism[String, (String, Char)](
        s => if(s.isEmpty) None else Some((s.init, s.last))){
        case (init, last) => init :+ last
      }
  }

  implicit val stringPlated: Plated[String] = new Plated[String] {
    val plate: Traversal[String, String] = new Traversal[String, String] {
      def modifyF[F[_]: Applicative](f: String => F[String])(s: String): F[String] =
        s.headOption match {
          case Some(h) => Applicative[F].map(f(s.tail))(h.toString ++ _)
          case None => Applicative[F].point("")
        }
    }
  }

  private def parseLong(s: String): Option[Long] = {
    // we reject cases where String will be an invalid Prism according 2nd Prism law
    // * String starts with +
    // * String starts with 0 and has multiple digits
    def inputBreaksPrismLaws(input: String): Boolean =
      s.isEmpty || s.startsWith("+") || (s.startsWith("0") && s.length > 1)

    if (inputBreaksPrismLaws(s)) None
    else s.toList match {
      case '-' :: xs => parseLongUnsigned(xs).map(-_)
      case        xs => parseLongUnsigned(xs)
    }
  }

  private def parseLongUnsigned(s: List[Char]): Option[Long] =
    if(s.isEmpty) None
    else s.traverse(charToDigit).map(_.foldl(0L)(n => d => n * 10 + d))

  private def charToDigit(c: Char): Option[Int] =
    if (c >= '0' && c <= '9') Some(c - '0')
    else None

  private def parseCaseSensitiveBoolean(stringBoolean: String): Option[Boolean] = stringBoolean match {
    case "true" => Some(true)
    case "false" => Some(false)
    case _ => None
  }
}
