package monocle.std

import monocle.SimplePrism
import monocle.function._

import scalaz.std.vector._

object vector extends VectorInstances

trait VectorInstances {

  implicit def vectorEmpty[A]: Empty[Vector[A]] = new Empty[Vector[A]] {
    def empty = SimplePrism[Vector[A], Unit](v => if(v.isEmpty) Some(()) else None, _ => Vector.empty)
  }

  implicit def vectorEach[A]: Each[Vector[A], A] = Each.traverseEach[Vector, A]

  implicit def vectorIndex[A]: Index[Vector[A], Int, A] =
    Index.traverseIndex[Vector, A](_.zipWithIndex)

  implicit def vectorFilterIndex[A]: FilterIndex[Vector[A], Int, A] =
    FilterIndex.traverseFilterIndex[Vector, A](_.zipWithIndex)

  implicit def vectorCons[A]: Cons[Vector[A], A] = new Cons[Vector[A], A]{
    def _cons = SimplePrism[Vector[A], (A, Vector[A])]({
      case Vector() => None
      case x +: xs  => Some(x, xs)
    }, { case (a, s) => a +: s })
  }

  implicit def vectorSnoc[A]: Snoc[Vector[A], A] = new Snoc[Vector[A], A]{
    def snoc = SimplePrism[Vector[A], (Vector[A], A)](
      v => if(v.isEmpty) None else Some((v.init, v.last)),
      {case (xs, x) => xs :+ x}
    )
  }
  implicit def vectorHeadOption[A]: HeadOption[Vector[A], A] =
    HeadOption.consHeadOption[Vector[A], A]

  implicit def vectorTailOption[A]: TailOption[Vector[A], Vector[A]] =
    TailOption.consTailOption[Vector[A], A]

  implicit def vectorLastOption[A]: LastOption[Vector[A], A] =
    LastOption.snocLastOption[Vector[A], A]

  implicit def vectorInitOption[A]: InitOption[Vector[A], Vector[A]] =
    InitOption.snocInitOption[Vector[A], A]

  implicit def vectorReverse[A]: Reverse[Vector[A], Vector[A]] =
    reverseFromReverseFunction[Vector[A]](_.reverse)

}
