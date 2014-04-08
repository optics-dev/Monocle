package monocle.function

import monocle.syntax.traversal._
import monocle.{Traversal, SimpleTraversal}
import scalaz.Applicative

trait FilterIndex[S, I, A] {

  /** Creates a Traversal from S to all A with an index matching the predicate */
  def filterIndex(predicate: I => Boolean): SimpleTraversal[S, A]

}

object FilterIndex extends FilterIndexInstances

trait FilterIndexInstances {

  def filterIndex[S, I, A](predicate: I => Boolean)
                            (implicit ev: FilterIndex[S, I, A]): SimpleTraversal[S, A] = ev.filterIndex(predicate)


  implicit def listFilterIndex[A] = new FilterIndex[List[A], Int, A] {
    def filterIndex(predicate: Int => Boolean) = new Traversal[List[A], List[A], A, A] {
      def multiLift[F[_] : Applicative](from: List[A], f: A => F[A]): F[List[A]] =
        scalaz.std.list.listInstance.traverseImpl(from.zipWithIndex){ case (a, j) =>
          if(predicate(j)) f(a) else Applicative[F].point(a)
        }
    }
  }

  implicit val stringFilterIndex = new FilterIndex[String, Int, Char]{
    def filterIndex(predicate: Int => Boolean) =
      monocle.std.string.stringToList |->> listFilterIndex.filterIndex(predicate)
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

}