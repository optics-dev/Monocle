package monocle.function

import monocle.syntax._
import monocle.{Traversal, SimpleTraversal}
import scalaz.{Traverse, IList, Applicative}
import scalaz.syntax.traverse._
import scalaz.std.list._
import scalaz.std.stream._
import scalaz.std.vector._
import scalaz.IList._

trait FilterIndex[S, I, A] {

  /** Creates a Traversal from S to all A with an index matching the predicate */
  def filterIndex(predicate: I => Boolean): SimpleTraversal[S, A]

}

object FilterIndex extends FilterIndexInstances

trait FilterIndexInstances {

  def filterIndex[S, I, A](predicate: I => Boolean)
                            (implicit ev: FilterIndex[S, I, A]): SimpleTraversal[S, A] = ev.filterIndex(predicate)


  implicit def listFilterIndex[A]   = traverseFilterIndex[List, A](_.zipWithIndex)
  implicit def streamFilterIndex[A] = traverseFilterIndex[Stream, A](_.zipWithIndex)
  implicit def vectorFilterIndex[A] = traverseFilterIndex[Vector, A](_.zipWithIndex)
  implicit def iListFilterIndex[A]  = traverseFilterIndex[IList, A](_.zipWithIndex)

  implicit val stringFilterIndex = new FilterIndex[String, Int, Char]{
    def filterIndex(predicate: Int => Boolean) =
      monocle.std.string.stringToList composeTraversal listFilterIndex.filterIndex(predicate)
  }

  implicit def mapFilterIndex[K, V] = new FilterIndex[Map[K, V], K, V] {
    def filterIndex(predicate: K => Boolean) = new Traversal[Map[K, V], Map[K, V], V, V] {
      def multiLift[F[_] : Applicative](from: Map[K, V], f: (V) => F[V]): F[Map[K, V]] =
        Applicative[F].map(
          scalaz.std.list.listInstance.traverseImpl(from.toList){ case (k, v) =>
            Applicative[F].map(if(predicate(k)) f(v) else Applicative[F].point(v))(k -> _)
          }
        )(_.toMap)
    }
  }

  def traverseFilterIndex[S[_]: Traverse, A](zipWithIndex: S[A] => S[(A, Int)]): FilterIndex[S[A], Int, A] = new FilterIndex[S[A], Int, A]{
    def filterIndex(predicate: Int => Boolean) = new Traversal[S[A], S[A], A, A] {
      def multiLift[F[_] : Applicative](from: S[A], f: A => F[A]): F[S[A]] =
        zipWithIndex(from).traverse { case (a, j) =>
          if(predicate(j)) f(a) else Applicative[F].point(a)
        }
    }
  }

}