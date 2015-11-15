package monocle.std

import monocle.function.{At, Empty}
import monocle.{Lens, Prism}

import scalaz.{ISet, Order}

object iset extends ISetOptics

trait ISetOptics {

  implicit def emptyISet[A]: Empty[ISet[A]] = new Empty[ISet[A]] {
    def empty = Prism[ISet[A], Unit](s => if(s.isEmpty) Some(()) else None)(_ => ISet.empty[A])
  }

  implicit def atISet[A: Order]: At[ISet[A], A, Option[Unit]] = new At[ISet[A], A, Option[Unit]] {
    def at(a: A) = Lens[ISet[A], Option[Unit]](s => if(s member a) Some(()) else None)(
      optA => set => optA.fold(set delete a)(_ => set insert a)
    )
  }

}
