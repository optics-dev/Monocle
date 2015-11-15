package monocle.std

import monocle.function.{At, Empty}
import monocle.{Lens, Prism}

object set extends SetOptics

trait SetOptics {

  implicit def emptySet[A]: Empty[Set[A]] = new Empty[Set[A]] {
    def empty = Prism[Set[A], Unit](s => if(s.isEmpty) Some(()) else None)(_ => Set.empty[A])
  }

  implicit def atSet[A]: At[Set[A], A, Option[Unit]] = new At[Set[A], A, Option[Unit]] {
    def at(a: A) = Lens[Set[A], Option[Unit]](s => if(s(a)) Some(()) else None)(
      optA => set => optA.fold(set - a)(_ => set + a)
    )
  }

}