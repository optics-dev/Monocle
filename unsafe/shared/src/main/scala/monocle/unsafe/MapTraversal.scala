package monocle.unsafe

import alleycats.std.all._
import cats.Applicative
import cats.instances.list._
import cats.syntax.applicative._
import cats.syntax.functor._
import cats.syntax.traverse._
import monocle.Traversal
import monocle.function.Each.fromTraverse
import monocle.function.{Each, FilterIndex}

import scala.collection.immutable.Map

object MapTraversal {

  implicit def mapEach[K, V]: Each[Map[K, V], V] = fromTraverse[Map[K, ?], V]

  implicit def mapMapFilterIndex[K, V]: FilterIndex[Map[K,V], K, V] = new FilterIndex[Map[K, V], K, V] {
    def filterIndex(predicate: K => Boolean) = new Traversal[Map[K, V], V] {
      def modifyF[F[_]: Applicative](f: V => F[V])(s: Map[K, V]): F[Map[K, V]] =
        s.toList.traverse{ case (k, v) =>
          (if(predicate(k)) f(v) else v.pure[F]).tupleLeft(k)
        }.map(kvs => Map(kvs: _*))
    }
  }

}
