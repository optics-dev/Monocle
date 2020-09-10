package monocle.std

import monocle.Optional
import monocle.POptional
import monocle.Prism

import cats.data.Ior.{Both, Right => That, Left => This}
import cats.data.Ior
import cats.syntax.either._

object these extends TheseOptics

trait TheseOptics {
  def theseToDisjunction[A, B]: Prism[Ior[A, B], Either[A, B]] =
    Prism[Ior[A, B], Either[A, B]] {
      case This(a)    => Some(a.asLeft[B])
      case That(b)    => Some(b.asRight[A])
      case Both(_, _) => None
    } {
      case Left(a)  => This(a)
      case Right(b) => That(b)
    }

  def pTheseLeft[A, B, C]: POptional[Ior[A, B], Ior[C, B], A, C] =
    POptional[Ior[A, B], Ior[C, B], A, C](ior => ior.fold(a => Right(a), b => Left(Ior.right(b)), (a, _) => Right(a)))(
      c => ior => ior.leftMap(_ => c)
    )

  def theseLeft[A, B]: Optional[Ior[A, B], A] = pTheseLeft[A, B, A]

  def pTheseRight[A, B, C]: POptional[Ior[A, B], Ior[A, C], B, C] =
    POptional[Ior[A, B], Ior[A, C], B, C](ior => ior.fold(a => Left(Ior.left(a)), b => Right(b), (_, b) => Right(b)))(
      c => ior => ior.map(_ => c)
    )

  def theseRight[A, B]: Optional[Ior[A, B], B] = pTheseRight[A, B, B]

  @deprecated("use theseToDisjunction", since = "1.2.0")
  def theseDisjunction[A, B]: Prism[Ior[A, B], Either[A, B]] =
    theseToDisjunction[A, B]
}
