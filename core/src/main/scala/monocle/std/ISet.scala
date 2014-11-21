package monocle.std

import monocle.{Lens, Prism}
import monocle.function.{At, Empty}

import scalaz.{ISet,Maybe,Order}

object iset extends ISetInstances

trait ISetInstances {

  implicit def emptyISet[A]: Empty[ISet[A]] = new Empty[ISet[A]] {
    def empty = Prism[ISet[A], Unit](s => if(s.isEmpty) Maybe.just(()) else Maybe.empty)(_ => ISet.empty[A])
  }

  implicit def atISet[A: Order]: At[ISet[A], A, Unit] = new At[ISet[A], A, Unit] {
    def at(a: A) = Lens[ISet[A], Maybe[Unit]](s => if(s member a) Maybe.just(()) else Maybe.empty)(
      maybeA => set => maybeA.cata(_ => set insert a, set delete a)
    )
  }

}
