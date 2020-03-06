package monocle.std

import java.net.URI
import java.util.UUID

import monocle.{Iso, Prism}

import scala.util.Try
import cats.instances.list._
import cats.instances.option._
import cats.syntax.traverse._

object string extends StringOptics

trait StringOptics extends PlatformSpecificStringOptics {
  val stringToList: Iso[String, List[Char]] =
    Iso((_: String).toList)(_.mkString)

  val stringToBoolean: Prism[String, Boolean] =
    Prism { s: String => parseCaseSensitiveBoolean(s) }(_.toString)

  val stringToLong: Prism[String, Long] =
    Prism(parseLong)(_.toString)

  val stringToInt: Prism[String, Int] =
    stringToLong composePrism long.longToInt

  val stringToByte: Prism[String, Byte] =
    stringToLong composePrism long.longToByte

  val stringToUUID: Prism[String, UUID] =
    Prism { s: String => Try(UUID.fromString(s)).toOption }(_.toString)

  val stringToURI: Prism[String, URI] =
    Prism { s: String => Try(new URI(s)).toOption }(_.toString)

  private def parseLong(s: String): Option[Long] = {
    // we reject cases where String will be an invalid Prism according 2nd Prism law
    // * String starts with +
    // * String starts with 0 and has multiple digits
    def inputBreaksPrismLaws(input: String): Boolean =
      s.isEmpty || s.startsWith("+") || (s.startsWith("0") && s.length > 1)

    if (inputBreaksPrismLaws(s)) None
    else
      s.toList match {
        case '-' :: xs => parseLongUnsigned(xs).map(-_)
        case xs        => parseLongUnsigned(xs)
      }
  }

  private def parseLongUnsigned(s: List[Char]): Option[Long] =
    if (s.isEmpty) None
    else s.traverse(charToDigit).map(_.foldLeft(0L)((n, d) => n * 10 + d))

  private def charToDigit(c: Char): Option[Int] =
    if (c >= '0' && c <= '9') Some(c - '0')
    else None

  private def parseCaseSensitiveBoolean(stringBoolean: String): Option[Boolean] = stringBoolean match {
    case "true"  => Some(true)
    case "false" => Some(false)
    case _       => None
  }
}
