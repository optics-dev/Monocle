package monocle.std

import monocle.function._
import monocle.{Optional, Prism}

import scalaz.Id.Id
import scalaz.Maybe
import scalaz.std.vector._
import scalaz.syntax.traverse._

object vector extends VectorInstances

trait VectorInstances {

  implicit def vectorEmpty[A]: Empty[Vector[A]] = new Empty[Vector[A]] {
    def empty = Prism[Vector[A], Unit](v => if(v.isEmpty) Maybe.just(()) else Maybe.empty)(_ => Vector.empty)
  }

  implicit def vectorEach[A]: Each[Vector[A], A] = Each.traverseEach[Vector, A]

  implicit def vectorIndex[A]: Index[Vector[A], Int, A] = new Index[Vector[A], Int, A] {
    def index(i: Int) = Optional[Vector[A], A](
      v      => if(i < 0) Maybe.empty else Maybe.fromTryCatchNonFatal(v.apply(i)))(
      (a, v) => v.zipWithIndex.traverse[Id, A]{
        case (_    , index) if index == i => a
        case (value, index)               => value
      }
    )
  }

  implicit def vectorFilterIndex[A]: FilterIndex[Vector[A], Int, A] =
    FilterIndex.traverseFilterIndex[Vector, A](_.zipWithIndex)

  implicit def vectorCons[A]: Cons[Vector[A], A] = new Cons[Vector[A], A]{
    def cons = Prism[Vector[A], (A, Vector[A])]{
      case Vector() => Maybe.empty
      case x +: xs  => Maybe.just((x, xs))
    }{ case (a, s) => a +: s }
  }

  implicit def vectorSnoc[A]: Snoc[Vector[A], A] = new Snoc[Vector[A], A]{
    def snoc = Prism[Vector[A], (Vector[A], A)](
      v => if(v.isEmpty) Maybe.empty else Maybe.just((v.init, v.last))){
      case (xs, x) => xs :+ x
    }
  }

  implicit def vectorReverse[A]: Reverse[Vector[A], Vector[A]] =
    reverseFromReverseFunction[Vector[A]](_.reverse)

}
