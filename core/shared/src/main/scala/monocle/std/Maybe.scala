package monocle.std

import monocle.function.{Each, Empty}
import monocle.{Iso, PIso, PPrism, Prism}

import scalaz.syntax.std.option._
import scalaz.{-\/, Maybe, \/-}

object maybe extends MaybeOptics

trait MaybeOptics {
  final def pMaybeToOption[A, B]: PIso[Maybe[A], Maybe[B], Option[A], Option[B]] =
    PIso((_: Maybe[A]).toOption)((_: Option[B]).toMaybe)

  final def maybeToOption[A]: Iso[Maybe[A], Option[A]] =
    pMaybeToOption[A, A]

  final def pJust[A, B]: PPrism[Maybe[A], Maybe[B], A, B] =
    PPrism[Maybe[A], Maybe[B], A, B](_.cata(\/-(_), -\/(Maybe.empty)))(Maybe.just[B])

  final def just[A]: Prism[Maybe[A], A] =
    pJust[A, A]

  final def nothing[A]: Prism[Maybe[A], Unit] =
    Prism[Maybe[A], Unit](m => if(m.isEmpty) Some(()) else None)(_ => Maybe.empty)

  implicit def maybeEach[A]: Each[Maybe[A], A] = new Each[Maybe[A], A]{
    def each = just.asTraversal
  }

  implicit def maybeEmpty[A]: Empty[Maybe[A]] = new Empty[Maybe[A]]{
    def empty = nothing
  }
}