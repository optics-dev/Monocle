package monocle.std

import monocle.function._
import monocle.{Iso, PIso, PPrism, Prism}

import scalaz.{-\/, Maybe, \/-}

object option extends OptionInstances

trait OptionFunctions {

  /** [[PIso]] between an [[scala.Option]] and a [[scalaz.Maybe]] */
  def pOptionToMaybe[A, B]: PIso[Option[A], Option[B], Maybe[A], Maybe[B]] =
    pMaybeToOption[B, A].reverse

  /** monomorphic alias for pOptionToMaybe */
  def optionToMaybe[A]: Iso[Option[A], Maybe[A]] =
    pOptionToMaybe[A, A]

  /** [[PPrism]] from a [[scala.Option]] to its [[scala.Some]] constructor */
  def pSome[A, B]: PPrism[Option[A], Option[B], A, B] =
    PPrism[Option[A], Option[B], A, B](_.map(\/-(_)) getOrElse -\/(None))(Some.apply)

  /** monomorphic alias for pSome */
  def some[A]: Prism[Option[A], A] =
    pSome[A, A]

  /** [[Prism]] from a [[scala.Option]] to its [[scala.None]] constructor */
  def none[A]: Prism[Option[A], Unit] =
    Prism[Option[A], Unit](opt => if (opt == None) Maybe.just(()) else Maybe.empty)(_ => None)

}

trait OptionInstances extends OptionFunctions {

  implicit def optionEmpty[A]: Empty[Option[A]] = new Empty[Option[A]] {
    def empty = none
  }

  implicit def optEach[A]: Each[Option[A], A] = new Each[Option[A], A] {
    def each = some.asTraversal
  }

}

