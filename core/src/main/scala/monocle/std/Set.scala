package monocle.std

import monocle.{SimpleLens, SimplePrism}
import monocle.function.{At, Empty}

import scalaz.Maybe

object set extends SetInstances

trait SetInstances {

  implicit def emptySet[A]: Empty[Set[A]] = new Empty[Set[A]] {
    def empty = SimplePrism[Set[A], Unit](s => if(s.isEmpty) Maybe.just(()) else Maybe.empty)(_ => Set.empty[A])
  }

  implicit def atSet[A]: At[Set[A], A, Unit] = new At[Set[A], A, Unit] {
    def at(a: A) = SimpleLens[Set[A], Maybe[Unit]](s => if(s(a)) Maybe.just(()) else Maybe.empty)(
      (maybeA, set) => maybeA.cata(_ => set + a, set - a)
    )
  }

}