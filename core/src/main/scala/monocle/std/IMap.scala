package monocle.std

import monocle.function._
import monocle.{Lens, Prism, Traversal}

import scalaz.std.list._
import scalaz.syntax.traverse._
import scalaz.{==>>, Applicative, Order}

object imap extends IMapOptics

trait IMapOptics {

  implicit def iMapEmpty[K, V]: Empty[K ==>> V] = new Empty[K ==>> V] {
    def empty = Prism[K ==>> V, Unit](m => if(m.isEmpty) Some(()) else None)(_ => ==>>.empty)
  }

  implicit def atIMap[K: Order, V]: At[K ==>> V, K, Option[V]] = new At[K ==>> V, K, Option[V]]{
    def at(i: K) = Lens{m: ==>>[K, V] => m.lookup(i)}(optV => map => optV.fold(map - i)(v => map + (i -> v)))
  }

  implicit def iMapEach[K, V]: Each[K ==>> V, V] = Each.traverseEach[({type λ[α] = K ==>> α})#λ, V]

  implicit def iMapIndex[K: Order, V]: Index[K ==>> V, K, V] = Index.atIndex

  implicit def iMapFilterIndex[K: Order, V]: FilterIndex[K ==>> V, K, V] = new FilterIndex[K ==>> V, K, V] {
    import scalaz.syntax.applicative._
    def filterIndex(predicate: K => Boolean) = new Traversal[K ==>> V, V] {
      def modifyF[F[_]: Applicative](f: V => F[V])(s: K ==>> V): F[K ==>> V] =
        s.toList.traverse{ case (k, v) =>
          (if(predicate(k)) f(v) else v.point[F]).strengthL(k)
        }.map(==>>.fromList(_))
    }
  }

}
