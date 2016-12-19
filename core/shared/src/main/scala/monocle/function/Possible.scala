package monocle.function

import monocle.{Iso, Prism}
import scalaz.{\/, Validation, Maybe}
import scala.util.{Try, Success, Failure}

/**
 * Typeclass that defines a [[Prism]] from a monomorphic container `S` to a possible `A` value.
 * @tparam S source of the [[Prism]]
 * @tparam A target of the [[Prism]], `A` is supposed to be unique for a given `S`
 */
abstract class Possible[S, A] extends Serializable {
  def possible: Prism[S, A]
}

trait PossibleFunctions {
  def possible[S, A](implicit ev: Possible[S, A]): Prism[S, A] = ev.possible
}

object Possible extends PossibleFunctions {
  /** lift an instance of [[Optional]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Possible[A, B]): Possible[S, B] = new Possible[S, B] {
    val possible: Prism[S, B] =
      iso composePrism ev.possible
  }

  /************************************************************************************************/
  /** Std instances                                                                               */
  /************************************************************************************************/
  implicit def optionPossible[A]: Possible[Option[A], A] = 
    new Possible[Option[A], A] { 
      def possible = monocle.std.option.some
    }

  implicit def eitherPossible[A,B]: Possible[Either[A,B], B] =
    new Possible[Either[A,B], B] { 
      def possible = monocle.std.either.stdRight
    }

  implicit def maybePossible[A,B]: Possible[Maybe[A], A] =
    new Possible[Maybe[A], A] { 
      def possible = monocle.std.maybe.just
    }

  implicit def disjunctionPossible[A,B]: Possible[A \/ B, B] =
    new Possible[A \/ B, B] { 
      def possible = monocle.std.disjunction.right
    }

  implicit def validationPossible[A,B]: Possible[Validation[A,B], B] =
    new Possible[Validation[A,B], B] { 
      def possible = monocle.std.validation.success
    }

  implicit def tryPossible[A]: Possible[Try[A], A] =
    new Possible[Try[A], A] { 
      def possible = monocle.std.utilTry.trySuccess
    }
}
