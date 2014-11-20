package monocle.std

import monocle.function._
import monocle.{Lens, Prism, Traversal}

import scalaz.std.list._
import scalaz.syntax.std.option._
import scalaz.syntax.traverse._
import scalaz.{Applicative, Maybe, Order, ==>>}

object imap extends IMapInstances

trait IMapInstances {

  implicit def iMapEmpty[K, V]: Empty[K ==>> V] = new Empty[K ==>> V] {
    def empty = Prism[K ==>> V, Unit](m => if(m.isEmpty) Maybe.just(()) else Maybe.empty)(_ => ==>>.empty)
  }

  implicit def atIMap[K: Order, V]: At[K ==>> V, K, V] = new At[K ==>> V, K, V]{
    def at(i: K) = Lens{m: ==>>[K, V] => m.lookup(i).toMaybe}((maybeV, map) => maybeV.cata(v => map + (i -> v), map - i))
  }

  implicit def iMapEach[K, V]: Each[K ==>> V, V] = Each.traverseEach[==>>[K, ?], V]

  implicit def iMapIndex[K: Order, V]: Index[K ==>> V, K, V] = Index.atIndex

  implicit def iMapFilterIndex[K: Order, V]: FilterIndex[K ==>> V, K, V] = new FilterIndex[K ==>> V, K, V] {
    import scalaz.syntax.applicative._
    def filterIndex(predicate: K => Boolean) = new Traversal[K ==>> V, V] {
      def _traversal[F[_]: Applicative](f: V => F[V])(s: K ==>> V): F[K ==>> V] =
        s.toList.traverse{ case (k, v) =>
          (if(predicate(k)) f(v) else v.point[F]).strengthL(k)
        }.map(==>>.fromList(_))
    }
  }

}
