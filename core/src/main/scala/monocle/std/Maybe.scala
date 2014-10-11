package monocle.std

import monocle.function.{Each, Empty}
import monocle.{Iso, Prism, SimplePrism}

import scalaz.{-\/, Maybe, \/-}


object maybe extends MaybeFunctions with MaybeInstances

trait MaybeFunctions {
  final def maybeToOption[A, B]: Iso[Maybe[A], Maybe[B], Option[A], Option[B]] =
    Iso.fromIsoFunctor[Maybe, Option, A, B](Maybe.optionMaybeIso.flip)

  final def just[A, B]: Prism[Maybe[A], Maybe[B], A, B] =
    Prism[Maybe[A], Maybe[B], A, B](_.cata(\/-(_), -\/(Maybe.empty)))(Maybe.just[B])

  final def nothing[A]: SimplePrism[Maybe[A], Unit] =
    SimplePrism[Maybe[A], Unit](m => if(m.isEmpty) Maybe.just(()) else Maybe.empty)(_ => Maybe.empty)
}

trait MaybeInstances extends MaybeFunctions {
  implicit def maybeEach[A]: Each[Maybe[A], A] = new Each[Maybe[A], A]{
    def each = just.asTraversal
  }

  implicit def maybeEmpty[A]: Empty[Maybe[A]] = new Empty[Maybe[A]]{
    def empty = nothing
  }
}