package monocle.function

import monocle.{Traversal, SimpleTraversal}
import scalaz.syntax.traverse._
import scalaz.{Traverse, Applicative}

trait FilterIndex[S, I, A] {

  /** Creates a Traversal from S to all A with an index matching the predicate */
  def filterIndex(predicate: I => Boolean): SimpleTraversal[S, A]

}

object FilterIndex extends FilterIndexFunctions

trait FilterIndexFunctions {

  def filterIndex[S, I, A](predicate: I => Boolean)
                            (implicit ev: FilterIndex[S, I, A]): SimpleTraversal[S, A] = ev.filterIndex(predicate)


  def traverseFilterIndex[S[_]: Traverse, A](zipWithIndex: S[A] => S[(A, Int)]): FilterIndex[S[A], Int, A] = new FilterIndex[S[A], Int, A]{
    def filterIndex(predicate: Int => Boolean) = new Traversal[S[A], S[A], A, A] {
      def multiLift[F[_] : Applicative](from: S[A], f: A => F[A]): F[S[A]] =
        zipWithIndex(from).traverse { case (a, j) =>
          if(predicate(j)) f(a) else Applicative[F].point(a)
        }
    }
  }

}