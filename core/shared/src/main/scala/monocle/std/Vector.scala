package monocle.std

import monocle.function._
import monocle.{Optional, Prism, Traversal}

import scalaz.Applicative
import scalaz.std.vector._

object vector extends VectorOptics

trait VectorOptics {

  implicit def vectorEmpty[A]: Empty[Vector[A]] = new Empty[Vector[A]] {
    def empty = Prism[Vector[A], Unit](v => if(v.isEmpty) Some(()) else None)(_ => Vector.empty)
  }

  implicit def vectorEach[A]: Each[Vector[A], A] = Each.traverseEach[Vector, A]

  implicit def vectorIndex[A]: Index[Vector[A], Int, A] = new Index[Vector[A], Int, A] {
    def index(i: Int) =
      Optional[Vector[A], A](v =>
        if(v.isDefinedAt(i)) Some(v(i))     else None)(a => v =>
        if(v.isDefinedAt(i)) v.updated(i,a) else v)
  }

  implicit def vectorFilterIndex[A]: FilterIndex[Vector[A], Int, A] =
    FilterIndex.traverseFilterIndex[Vector, A](_.zipWithIndex)

  implicit def vectorCons[A]: Cons[Vector[A], A] = new Cons[Vector[A], A]{
    def cons = Prism[Vector[A], (A, Vector[A])]{
      case Vector() => None
      case x +: xs  => Some((x, xs))
    }{ case (a, s) => a +: s }
  }

  implicit def vectorSnoc[A]: Snoc[Vector[A], A] = new Snoc[Vector[A], A]{
    def snoc = Prism[Vector[A], (Vector[A], A)](
      v => if(v.isEmpty) None else Some((v.init, v.last))){
      case (xs, x) => xs :+ x
    }
  }

  implicit def vectorReverse[A]: Reverse[Vector[A], Vector[A]] =
    Reverse.reverseFromReverseFunction[Vector[A]](_.reverse)

  implicit def vectorPlated[A]: Plated[Vector[A]] = new Plated[Vector[A]] {
    val plate: Traversal[Vector[A], Vector[A]] = new Traversal[Vector[A], Vector[A]] {
      def modifyF[F[_]: Applicative](f: Vector[A] => F[Vector[A]])(s: Vector[A]): F[Vector[A]] =
        s match {
          case h +: t => Applicative[F].map(f(t))(h +: _)
          case _ => Applicative[F].point(Vector.empty)
        }
    }
  }

}
