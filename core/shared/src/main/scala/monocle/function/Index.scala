package monocle.function

import monocle.Optional

import scala.util.Try

trait Index[From] {
  type Index
  type To

  def index(index: Index): Optional[From, To]
}

object Index {
  type Aux[From, _Index, _To] = Index[From] { type Index = _Index; type To = _To }

  def apply[From, _Index, _To](f: _Index => Optional[From, _To]): Aux[From, _Index, _To] =
    new Index[From] {
      type Index = _Index
      type To    = _To
      def index(index: _Index): Optional[From, _To] = f(index)
    }

  implicit def list[A]: Aux[List[A], Int, A] =
    apply(
      (i: Int) =>
        if (i < 0)
          Optional.void
        else
          Optional[List[A], A](xs => Try(xs(i)).toOption)((xs, a) => Try(xs.updated(i, a)).getOrElse(xs))
    )

  implicit def vector[A]: Aux[Vector[A], Int, A] =
    apply(
      (i: Int) =>
        if (i < 0)
          Optional.void
        else
          Optional[Vector[A], A](xs => Try(xs(i)).toOption)((xs, a) => Try(xs.updated(i, a)).getOrElse(xs))
    )

  implicit def map[K, V]: Aux[Map[K, V], K, V] = At.map
}
