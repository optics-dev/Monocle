package monocle.function

import monocle.{Iso, Traversal}

import scala.annotation.implicitNotFound
import scala.collection.immutable.SortedMap
import cats.syntax.traverse._
import cats.{Applicative, Order, Traverse}

/** Typeclass that defines a [[Traversal]] from an `S` to all its elements `A` whose index `I` in `S` satisfies the
  * predicate
  * @tparam S
  *   source of [[Traversal]]
  * @tparam I
  *   index
  * @tparam A
  *   target of [[Traversal]], `A` is supposed to be unique for a given pair `(S, I)`
  */
@implicitNotFound(
  "Could not find an instance of FilterIndex[${S},${I},${A}], please check Monocle instance location policy to " + "find out which import is necessary"
)
abstract class FilterIndex[S, I, A] extends Serializable {
  def filterIndex(predicate: I => Boolean): Traversal[S, A]
}

trait FilterIndexFunctions {
  def filterIndex[S, I, A](predicate: I => Boolean)(implicit ev: FilterIndex[S, I, A]): Traversal[S, A] =
    ev.filterIndex(predicate)
}

object FilterIndex extends FilterIndexFunctions {

  /** lift an instance of [[FilterIndex]] using an [[Iso]] */
  def fromIso[S, A, I, B](iso: Iso[S, A])(implicit ev: FilterIndex[A, I, B]): FilterIndex[S, I, B] =
    new FilterIndex[S, I, B] {
      def filterIndex(predicate: I => Boolean): Traversal[S, B] =
        iso.andThen(ev.filterIndex(predicate))
    }

  def fromTraverse[S[_]: Traverse, A](zipWithIndex: S[A] => S[(A, Int)]): FilterIndex[S[A], Int, A] =
    new FilterIndex[S[A], Int, A] {
      def filterIndex(predicate: Int => Boolean) =
        new Traversal[S[A], A] {
          def modifyA[F[_]: Applicative](f: A => F[A])(s: S[A]): F[S[A]] =
            zipWithIndex(s).traverse { case (a, j) =>
              if (predicate(j)) f(a) else Applicative[F].pure(a)
            }
        }
    }

  /** *********************************************************************************************
    */
  /** Std instances */
  /** *********************************************************************************************
    */

  implicit def listFilterIndex[A]: FilterIndex[List[A], Int, A] =
    fromTraverse(_.zipWithIndex)

  implicit def lazyListFilterIndex[A]: FilterIndex[LazyList[A], Int, A] =
    fromTraverse(_.zipWithIndex)

  implicit def mapFilterIndex[K, V]: FilterIndex[Map[K, V], K, V] =
    new FilterIndex[Map[K, V], K, V] {
      import cats.syntax.applicative._
      import cats.syntax.functor._

      def filterIndex(predicate: K => Boolean) =
        new Traversal[Map[K, V], V] {
          def modifyA[F[_]: Applicative](f: V => F[V])(s: Map[K, V]): F[Map[K, V]] =
            s.toList
              .traverse { case (k, v) =>
                (if (predicate(k)) f(v) else v.pure[F]).tupleLeft(k)
              }
              .map(_.toMap)
        }
    }

  implicit def sortedMapFilterIndex[K, V](implicit ok: Order[K]): FilterIndex[SortedMap[K, V], K, V] =
    new FilterIndex[SortedMap[K, V], K, V] {
      import cats.syntax.applicative._
      import cats.syntax.functor._

      def filterIndex(predicate: K => Boolean) =
        new Traversal[SortedMap[K, V], V] {
          def modifyA[F[_]: Applicative](f: V => F[V])(s: SortedMap[K, V]): F[SortedMap[K, V]] =
            s.toList
              .traverse { case (k, v) =>
                (if (predicate(k)) f(v) else v.pure[F]).tupleLeft(k)
              }
              .map(kvs => SortedMap(kvs*)(ok.toOrdering))
        }
    }

  implicit val stringFilterIndex: FilterIndex[String, Int, Char] =
    new FilterIndex[String, Int, Char] {
      def filterIndex(predicate: Int => Boolean) =
        monocle.std.string.stringToList.andThen(FilterIndex.filterIndex[List[Char], Int, Char](predicate))
    }

  implicit def vectorFilterIndex[A]: FilterIndex[Vector[A], Int, A] =
    fromTraverse(_.zipWithIndex)

  /** *********************************************************************************************
    */
  /** Cats instances */
  /** *********************************************************************************************
    */
  import cats.data.{Chain, NonEmptyChain, NonEmptyList, NonEmptyVector}

  implicit def chainFilterIndex[A]: FilterIndex[Chain[A], Int, A] =
    fromTraverse(_.zipWithIndex)

  implicit def necFilterIndex[A]: FilterIndex[NonEmptyChain[A], Int, A] =
    fromTraverse(_.zipWithIndex)

  implicit def nelFilterIndex[A]: FilterIndex[NonEmptyList[A], Int, A] =
    fromTraverse(_.zipWithIndex)

  implicit def nevFilterIndex[A]: FilterIndex[NonEmptyVector[A], Int, A] =
    fromTraverse(_.zipWithIndex)
}
