package monocle.std

import monocle.function.{Each, Empty}
import monocle.{PIso, PPrism, Prism}

import scalaz.syntax.std.option._
import scalaz.{-\/, Maybe, \/-}


object maybe extends MaybeFunctions with MaybeInstances

trait MaybeFunctions {
  final def maybeToOption[A, B]: PIso[Maybe[A], Maybe[B], Option[A], Option[B]] =
    PIso((_: Maybe[A]).toOption)((_: Option[B]).toMaybe)

  final def just[A, B]: PPrism[Maybe[A], Maybe[B], A, B] =
    PPrism[Maybe[A], Maybe[B], A, B](_.cata(\/-(_), -\/(Maybe.empty)))(Maybe.just[B])

  final def nothing[A]: Prism[Maybe[A], Unit] =
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