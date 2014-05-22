package monocle.function

import monocle.syntax._
import monocle.{Traversal, SimpleTraversal}
import scalaz.{IList, Applicative}
import scalaz.syntax.traverse._

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

  implicit def streamFilterIndex[A] = new FilterIndex[Stream[A], Int, A] {
    def filterIndex(predicate: Int => Boolean) = new Traversal[Stream[A], Stream[A], A, A] {
      def multiLift[F[_] : Applicative](from: Stream[A], f: A => F[A]): F[Stream[A]] =
        scalaz.std.stream.streamInstance.traverseImpl(from.zipWithIndex){ case (a, j) =>
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

  implicit def vectorFilterIndex[A] = new FilterIndex[Vector[A], Int, A] {
    def filterIndex(predicate: Int => Boolean) = new Traversal[Vector[A], Vector[A], A, A] {
      def multiLift[F[_] : Applicative](from: Vector[A], f: A => F[A]): F[Vector[A]] =
        scalaz.std.vector.vectorInstance.traverseImpl(from.zipWithIndex){ case (a, j) =>
          if(predicate(j)) f(a) else Applicative[F].point(a)
        }
    }
  }

  implicit def iListFilterIndex[A] = new FilterIndex[IList[A], Int, A] {
    def filterIndex(predicate: Int => Boolean) = new Traversal[IList[A], IList[A], A, A] {
      def multiLift[F[_] : Applicative](from: IList[A], f: A => F[A]): F[IList[A]] =
        from.zipWithIndex.traverse{ case (a, j) =>
          if(predicate(j)) f(a) else Applicative[F].point(a)
        }
    }
  }

}