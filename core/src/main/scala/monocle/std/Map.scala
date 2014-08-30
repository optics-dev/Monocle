package monocle.std

import monocle.function._
import monocle.{SimpleTraversal, SimplePrism, Traversal, SimpleLens}
import scalaz.Maybe.Just
import scalaz.{Kleisli, Maybe, Applicative}
import scalaz.std.list._
import scalaz.std.map._
import scalaz.syntax.traverse._

object map extends MapInstances

trait MapInstances {

  implicit def mapEmpty[K, V]: Empty[Map[K, V]] = new Empty[Map[K, V]] {
    def empty = SimplePrism[Map[K, V], Unit](m => if(m.isEmpty) Maybe.just(()) else Maybe.empty, _ => Map.empty)
  }

  implicit def atMap[K, V]: At[Map[K, V], K, V] = new At[Map[K, V], K, V]{
    def at(i: K) = SimpleLens[Map[K, V], Maybe[V]](
      m => Maybe.optionMaybeIso.to(m.get(i)),
      (map, maybeV) => maybeV.cata(v => map + (i -> v), map - i)
    )
  }

  implicit def mapEach[K, V]: Each[Map[K, V], V] = Each.traverseEach[({type λ[α] = Map[K,α]})#λ, V]

  implicit def mapIndex[K, V]: Index[Map[K, V], K  , V] = Index.atIndex

  implicit def mapFilterIndex[K, V]: FilterIndex[Map[K,V], K, V] = new FilterIndex[Map[K, V], K, V] {
    import scalaz.syntax.applicative._
    def filterIndex(predicate: K => Boolean) = new SimpleTraversal[Map[K, V], V] {
      def _traversal[F[_]: Applicative](f: Kleisli[F, V, V]) = Kleisli[F, Map[K, V], Map[K, V]](s =>
        s.toList.traverse{ case (k, v) =>
          (if(predicate(k)) f(v) else v.point[F]).map(k -> _)
        }.map(_.toMap)
      )
    }
  }

}
