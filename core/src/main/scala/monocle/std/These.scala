package monocle.std

import monocle.Prism

import scalaz.\&/.{Both, That, This}
import scalaz.{-\/, Maybe, \&/, \/, \/-}

object these extends TheseFunctions

trait TheseFunctions {
  /** [[Prism]] between a [[scalaz.\&/]] and a [[scalaz.Disjunction]]  */
  def theseToDisjunction[A, B]: Prism[A \&/ B, A \/ B] =
    Prism[A \&/ B, A \/ B]{
      case This(a) => Maybe.just(\/.left(a))
      case That(b) => Maybe.just(\/.right(b))
      case Both(_, _) => Maybe.empty[A \/ B]
    }{
      case -\/(a) => This(a)
      case \/-(b) => That(b)
    }

  /** [[Prism]] from a [[scalaz.\&/]] to its [[scalaz.\&/.This]] constructor */
  def theseToThis[A, B]: Prism[A \&/ B, A] =
    Prism[A \&/ B, A]{
      case This(a) => Maybe.just(a)
      case _       => Maybe.empty
    }(This.apply)

  /** [[Prism]] from a [[scalaz.\&/]] to its [[scalaz.\&/.This]] constructor */
  def theseToThat[A, B]: Prism[A \&/ B, B] =
    Prism[A \&/ B, B]{
      case That(b) => Maybe.just(b)
      case _       => Maybe.empty
    }(That.apply)

  /** [[Prism]] from a [[scalaz.\&/]] to its [[scalaz.\&/.Both]] constructor */
  def theseToBoth[A, B]: Prism[A \&/ B, (A, B)] =
    Prism[A \&/ B, (A, B)]{
      case Both(a, b) => Maybe.just((a, b))
      case _          => Maybe.empty
    }{ case (a, b) => Both(a, b) }
}
