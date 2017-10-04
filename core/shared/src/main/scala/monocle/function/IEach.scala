package monocle.function

import scalaz.Applicative
import scalaz.syntax.traverse._

import monocle.{Indexable, ITraversal}
import Indexable.index

abstract class IEach[I, S, A] {
  def iEach: ITraversal[I, S, A]
}

object IEach {
  import scalaz.std.list._

  def iEach[I, S, A](implicit ev: IEach[I, S, A]): ITraversal[I, S, A] = ev.iEach

  implicit def mapIEach[K, V]: IEach[K, Map[K, V], V] = new IEach[K, Map[K, V], V] {
    def iEach: ITraversal[K, Map[K, V], V] = new ITraversal[K, Map[K, V], V] {
      def modifyF[
            F[_]: Applicative,
            P[_, _]: Indexable[K, ?[_, _]]](
          f: P[V, F[V]])(s: Map[K, V]): F[Map[K, V]] =
        s.toList.traverse { case (k, v) =>
          index(f)(k)(v).strengthL(k)
        }.map(_.toMap)
    }
  }
}
