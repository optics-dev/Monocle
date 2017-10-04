package monocle.function

import scalaz.Applicative
import scalaz.syntax.traverse._

import monocle.{Indexable, ITraversal}
import Indexable.index

abstract class IFilterIndex[S, I, A] {
  def iFilterIndex(predicate: I => Boolean): ITraversal[I, S, A]
}

object IFilterIndex {
  import scalaz.std.list._

  def iFilterIndex[S, I, A](predicate: I => Boolean)(implicit ev: IFilterIndex[S, I, A]): ITraversal[I, S, A] =
    ev.iFilterIndex(predicate)

  implicit def mapIFilterIndex[K, V]: IFilterIndex[Map[K, V], K, V] = new IFilterIndex[Map[K, V], K, V] {
    import scalaz.syntax.applicative._
    def iFilterIndex(predicate: K => Boolean) = new ITraversal[K, Map[K, V], V] {
      def modifyF[
            F[_]: Applicative,
            P[_, _]: Indexable[K, ?[_, _]]](
          f: P[V, F[V]])(s: Map[K, V]): F[Map[K, V]] =
        s.toList.traverse { case (k, v) =>
          (if (predicate(k)) index(f)(k)(v) else v.pure[F]).strengthL(k)
        }.map(_.toMap)
    }
  }
}
