package monocle.function

import monocle.Optional

import scala.util.Try

trait Index[A] {
  type I
  type B

  def index(index: I): Optional[A, B]
}

object Index {
  type Aux[A, I0, B0] = Index[A] { type I = I0; type B = B0 }

  def apply[A, I0, B0](f: I0 => Optional[A, B0]): Aux[A, I0, B0] =
    new Index[A] {
      type I = I0
      type B = B0
      def index(index: I0): Optional[A, B0] = f(index)
    }

  implicit def list[A]: Aux[List[A], Int, A] =
    apply(
      (i: Int) =>
        if (i < 0)
          Optional.void
        else
          Optional[List[A], A](xs => Try(xs(i)).toOption)((xs, a) =>
            Try(xs.updated(i, a)).getOrElse(xs)))

  implicit def vector[A]: Aux[Vector[A], Int, A] =
    apply(
      (i: Int) =>
        if (i < 0)
          Optional.void
        else
          Optional[Vector[A], A](xs => Try(xs(i)).toOption)((xs, a) =>
            Try(xs.updated(i, a)).getOrElse(xs)))

  implicit def map[K, V]: Aux[Map[K, V], K, V] = At.map
}
