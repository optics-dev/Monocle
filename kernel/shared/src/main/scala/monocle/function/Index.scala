package monocle.function

import monocle.Optional

import scala.util.Try

trait Index[S] {
  type I
  type A

  def index(index: I): Optional[S, A]
}

object Index {
  type Aux[S, I0, A0] = Index[S] { type I = I0; type A = A0 }

  def apply[S, I0, A0](f : I0 => Optional[S, A0]): Aux[S, I0, A0] =
    new Index[S] {
      type I = I0
      type A = A0
      def index(index: I0): Optional[S, A0] = f(index)
    }

  implicit def list[A]: Aux[List[A], Int, A] =
    apply((i: Int) =>
      if (i < 0)
        Optional.void
      else
        Optional[List[A], A](xs => Try(xs(i)).toOption)((xs, a) => Try(xs.updated(i, a)).getOrElse(xs))
    )

  implicit def map[K, V]: Aux[Map[K, V], K, V] = At.map
}