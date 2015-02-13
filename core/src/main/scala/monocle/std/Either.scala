package monocle.std

import monocle.{Iso, PIso, PPrism, Prism}

import scalaz.{\/, -\/, \/-}

object either extends StdEitherFunctions

trait StdEitherFunctions {

  /** [[PIso]] between an [[scala.Either]] and a [[scalaz.Disjunction]] */
  def pEitherToDisjunction[A, B, C, D]: PIso[Either[A, B], Either[C, D], A \/ B, C \/ D] =
    pDisjunctionToEither[C, D, A, B].reverse

  /** monomorphic alias for pEitherToDisjunction */
  def eitherToDisjunction[A, B]: Iso[Either[A, B], A \/ B] =
    pEitherToDisjunction[A, B, A, B]

  /** [[PPrism]] toward the left side of an [[scala.Either]] */
  def pStdLeft[A, B, C]: PPrism[Either[A, B], Either[C, B], A, C] =
    PPrism[Either[A, B], Either[C, B], A, C]{
      case Left(a)  => \/-(a)
      case Right(b) => -\/(Right(b))
    }(Left.apply)

  /** monomorphic alias for pStdLeft */
  def stdLeft[A, B]: Prism[Either[A, B], A] =
    pStdLeft[A, B, A]

  /** [[PPrism]] toward the right side of an [[scala.Either]] */
  def pStdRight[A, B, C]: PPrism[Either[A, B], Either[A, C], B, C] =
    PPrism[Either[A, B], Either[A, C], B, C]{
      case Left(a)  => -\/(Left(a))
      case Right(b) => \/-(b)
    }(Right.apply)

  /** monomorphic alias for pStdRight */
  def stdRight[A, B]: Prism[Either[A, B], B] =
    pStdRight[A, B, B]

}
