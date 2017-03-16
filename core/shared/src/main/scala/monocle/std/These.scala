package monocle.std

import monocle.Prism

import cats.data.Ior.{Both, Right => That, Left => This}
import cats.data.{Ior => \&/}
import cats.syntax.either._
import scala.{Left => -\/, Either => \/, Right => \/-}

object these extends TheseOptics

trait TheseOptics {
  def theseToDisjunction[A, B]: Prism[A \&/ B, A \/ B] = Prism[A \&/ B, A \/ B]{
    case This(a) => Some(a.asLeft[B])
    case That(b) => Some(b.asRight[A])
    case Both(_, _) => None
  }{
    case -\/(a) => This(a)
    case \/-(b) => That(b)
  }

  @deprecated("use theseToDisjunction", since = "1.2.0")
  def theseDisjunction[A, B]: Prism[A \&/ B, A \/ B] =
    theseToDisjunction[A, B]
}
