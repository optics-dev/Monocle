package monocle.std

import monocle.{Prism, PPrism}
import scala.{Left => -\/, Right => \/-}

object either extends EitherOptics

trait EitherOptics {
  
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
}
