package monocle.std

import monocle.function._
import monocle.{Traversal, SimpleLens}
import scalaz.Applicative
import scalaz.std.list._
import scalaz.std.map._
import scalaz.syntax.traverse._

object map extends MapInstances

trait MapInstances {

  implicit def atMap[K, V]: At[Map[K, V], K, V] = new At[Map[K, V], K, V]{
    def at(i: K) = SimpleLens[Map[K, V], Option[V]](
      _.get(i),
      (map, optValue) => optValue match {
        case Some(value) => map + (i -> value)
        case None        => map - i
      })
  }

  implicit def mapEach[K, V]: Each[Map[K, V], V] = Each.traverseEach[({type λ[α] = Map[K,α]})#λ, V]

  implicit def mapIndex[K, V]: Index[Map[K, V], K  , V] = Index.atIndex

  implicit def mapFilterIndex[K, V]: FilterIndex[Map[K,V], K, V] = new FilterIndex[Map[K, V], K, V] {
    def filterIndex(predicate: K => Boolean) = new Traversal[Map[K, V], Map[K, V], V, V] {
      def multiLift[F[_] : Applicative](from: Map[K, V], f: (V) => F[V]): F[Map[K, V]] =
        Applicative[F].map(
          from.toList.traverse{ case (k, v) =>
            Applicative[F].map(if(predicate(k)) f(v) else Applicative[F].point(v))(k -> _)
          }
        )(_.toMap)
    }
  }

}
