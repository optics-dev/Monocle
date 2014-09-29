package monocle.std

import monocle.function._
import monocle.{SimpleLens, SimplePrism, SimpleTraversal}

import scalaz.std.list._
import scalaz.std.map._
import scalaz.syntax.std.option._
import scalaz.syntax.traverse._
import scalaz.{Applicative, Maybe}

object map extends MapInstances

trait MapInstances {

  implicit def mapEmpty[K, V]: Empty[Map[K, V]] = new Empty[Map[K, V]] {
    def empty = SimplePrism[Map[K, V], Unit](m => if(m.isEmpty) Maybe.just(()) else Maybe.empty, _ => Map.empty)
  }

  implicit def atMap[K, V]: At[Map[K, V], K, V] = new At[Map[K, V], K, V]{
    def at(i: K) = SimpleLens[Map[K, V], Maybe[V]](
      m => m.get(i).toMaybe,
      (maybeV, map) => maybeV.cata(v => map + (i -> v), map - i)
    )
  }

  implicit def mapEach[K, V]: Each[Map[K, V], V] = Each.traverseEach[Map[K, ?], V]

  implicit def mapIndex[K, V]: Index[Map[K, V], K  , V] = Index.atIndex

  implicit def mapFilterIndex[K, V]: FilterIndex[Map[K,V], K, V] = new FilterIndex[Map[K, V], K, V] {
    import scalaz.syntax.applicative._
    def filterIndex(predicate: K => Boolean) = new SimpleTraversal[Map[K, V], V] {
      def _traversal[F[_]: Applicative](f: V => F[V])(s: Map[K, V]): F[Map[K, V]] =
        s.toList.traverse{ case (k, v) =>
          (if(predicate(k)) f(v) else v.point[F]).strengthL(k)
        }.map(_.toMap)
    }
  }

}
