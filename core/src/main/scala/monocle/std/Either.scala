package monocle.std

import monocle.{Prism, PPrism, Iso, PIso}
import scalaz.{\/, -\/, \/-}

object either extends StdEitherFunctions

trait StdEitherFunctions {
  
  final def pStdLeft[A, B, C]: PPrism[Either[A, B], Either[C, B], A, C] =
    PPrism[Either[A, B], Either[C, B], A, C]{
      case Left(a)  => \/-(a)
      case Right(b) => -\/(Right(b))
    }(Left.apply)

  final def stdLeft[A, B]: Prism[Either[A, B], A] =
    pStdLeft[A, B, A]

  final def pStdRight[A, B, C]: PPrism[Either[A, B], Either[A, C], B, C] =
    PPrism[Either[A, B], Either[A, C], B, C]{
      case Left(a)  => -\/(Left(a))
      case Right(b) => \/-(b)
    }(Right.apply)

  final def stdRight[A, B]: Prism[Either[A, B], B] =
    pStdRight[A, B, B]

  final def pEitherToDisjunction[E1, E2, A1, A2]: PIso[Either[E1, A1], Either[E2, A2], E1 \/ A1, E2 \/ A2] =
    pDisjunctionToEither[E2, E1, A2, A1].reverse

  final def eitherToDisjunction[E, A]: Iso[Either[E, A], E \/ A] =
    pEitherToDisjunction[E, E, A, A]
}
