package monocle.std

import monocle.{Prism}
import scalaz.{Validation, Maybe, Success, Failure}
import Maybe.{Just, Empty}

object validation extends ValidationFunctions

trait ValidationFunctions {

  def success[E, A]: Prism[Validation[E, A], A] =
    Prism[Validation[E, A], A](_.toMaybe)(Success.apply)

  def failure[E, A]: Prism[Validation[E, A], E] =
    Prism[Validation[E, A], E]{
      case Success(a) => Empty()
      case Failure(e) => Just(e)
    }(Failure.apply)

}