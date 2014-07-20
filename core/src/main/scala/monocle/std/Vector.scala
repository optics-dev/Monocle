package monocle.std

import monocle.function._
import monocle.{Optional, SimpleOptional}
import scalaz.Applicative
import scalaz.std.vector._

object vector extends VectorInstances

trait VectorInstances {

  implicit def vectorEach[A]: Each[Vector[A], A] = Each.traverseEach[Vector, A]

  implicit def vectorIndex[A]: Index[Vector[A], Int, A] =
    Index.traverseIndex[Vector, A](_.zipWithIndex)

  implicit def vectorFilterIndex[A]: FilterIndex[Vector[A], Int, A] =
    FilterIndex.traverseFilterIndex[Vector, A](_.zipWithIndex)

  implicit def vectorHeadOption[A]: HeadOption[Vector[A], A] = new HeadOption[Vector[A], A] {
    def headOption = SimpleOptional[Vector[A], A](_.headOption, (vector, a) =>
      if(vector.isEmpty) vector else a +: vector.tail
    )
  }

  implicit def vectorTailOption[A]: TailOption[Vector[A], Vector[A]] = new TailOption[Vector[A], Vector[A]]{
    def tailOption = new Optional[Vector[A], Vector[A], Vector[A], Vector[A]] {
      def multiLift[F[_] : Applicative](from: Vector[A], f: Vector[A] => F[Vector[A]]): F[Vector[A]] = from match {
        case Vector() => Applicative[F].point(Vector[A]())
        case x +: xs  => Applicative[F].map(f(xs))(x +: _)
      }
    }
  }

  implicit def vectorLastOption[A]: LastOption[Vector[A], A] =
    LastOption.reverseHeadLastOption[Vector[A], A]

  implicit def vectorInitOption[A]: InitOption[Vector[A], Vector[A]] =
    InitOption.reverseTailInitOption[Vector[A]]

  implicit def vectorReverse[A]: Reverse[Vector[A], Vector[A]] =
    Reverse.simple[Vector[A]](_.reverse)

}
