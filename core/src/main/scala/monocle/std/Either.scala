package monocle.std

import monocle.Prism
import scalaz.{ -\/, \/- }

object either extends StdEitherFunctions

trait StdEitherFunctions {
  
  def stdLeft[A, B, C]: Prism[Either[A, B], Either[C, B], A, C] =
    Prism[Either[A, B], Either[C, B], A, C]{
      case Left(a)  => \/-(a)
      case Right(b) => -\/(Right(b))
    }(Left.apply)

  def stdRight[A, B, C]: Prism[Either[A, B], Either[A, C], B, C] =
    Prism[Either[A, B], Either[A, C], B, C]{
      case Left(a)  => -\/(Left(a))
      case Right(b) => \/-(b)
    }(Right.apply)

}
