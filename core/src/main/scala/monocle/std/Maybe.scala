package monocle.std

import monocle.function.{Each, Empty}
import monocle.{Iso, PIso, PPrism, Prism}

import scalaz.syntax.std.option._
import scalaz.{-\/, Maybe, \/-}


object maybe extends MaybeFunctions with MaybeInstances

trait MaybeFunctions {

  /** [[PIso]] between a [[scalaz.Maybe]] and a [[scala.Option]] */
  def pMaybeToOption[A, B]: PIso[Maybe[A], Maybe[B], Option[A], Option[B]] =
    PIso((_: Maybe[A]).toOption)((_: Option[B]).toMaybe)

  /** monomorphic alias for pMaybeToOption */
  def maybeToOption[A]: Iso[Maybe[A], Option[A]] =
    pMaybeToOption[A, A]

  /** [[PPrism]] from a [[scalaz.Maybe]] to its [[scalaz.Maybe.Just]] constructor */
  def pJust[A, B]: PPrism[Maybe[A], Maybe[B], A, B] =
    PPrism[Maybe[A], Maybe[B], A, B](_.cata(\/-(_), -\/(Maybe.empty)))(Maybe.just[B])

  /** monomorphic alias for pJust */
  def just[A]: Prism[Maybe[A], A] =
    pJust[A, A]

  /** [[Prism]] from a [[scalaz.Maybe]] to its [[scalaz.Maybe.Empty]] constructor */
  def nothing[A]: Prism[Maybe[A], Unit] =
    Prism[Maybe[A], Unit](m => if(m.isEmpty) Maybe.just(()) else Maybe.empty)(_ => Maybe.empty)
}

trait MaybeInstances extends MaybeFunctions {
  implicit def maybeEach[A]: Each[Maybe[A], A] = new Each[Maybe[A], A]{
    def each = just.asTraversal
  }

  implicit def maybeEmpty[A]: Empty[Maybe[A]] = new Empty[Maybe[A]]{
    def empty = nothing
  }
}