package monocle.std

import monocle.{SimpleLens, SimplePrism}
import monocle.function.{At, Empty}

object set extends SetInstances

trait SetInstances {

  implicit def emptySet[A]: Empty[Set[A]] = new Empty[Set[A]] {
    def empty = SimplePrism[Set[A], Unit](s => if(s.isEmpty) Some(()) else None, _ => Set.empty[A])
  }

  implicit def atSet[A]: At[Set[A], A, Unit] = new At[Set[A], A, Unit] {
    def at(a: A) = SimpleLens[Set[A], Option[Unit]](
      s => if(s(a)) Some(()) else None,
      (set, optA) => optA match {
        case None     => set - a
        case Some(()) => set + a
    })
  }

}