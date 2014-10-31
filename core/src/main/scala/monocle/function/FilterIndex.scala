package monocle.function

import monocle.Traversal

import scala.annotation.implicitNotFound
import scalaz.syntax.traverse._
import scalaz.{Applicative, Traverse}

@implicitNotFound("Could not find an instance of FilterIndex[${S},${I},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait FilterIndex[S, I, A] {

  /** Creates a Traversal from S to all A with an index matching the predicate */
  def filterIndex(predicate: I => Boolean): Traversal[S, A]

}

object FilterIndex extends FilterIndexFunctions

trait FilterIndexFunctions {

  def filterIndex[S, I, A](predicate: I => Boolean)
                            (implicit ev: FilterIndex[S, I, A]): Traversal[S, A] = ev.filterIndex(predicate)


  def traverseFilterIndex[S[_]: Traverse, A](zipWithIndex: S[A] => S[(A, Int)]): FilterIndex[S[A], Int, A] = new FilterIndex[S[A], Int, A]{
    def filterIndex(predicate: Int => Boolean) = new Traversal[S[A], A] {
      def _traversal[F[_]: Applicative](f: A => F[A])(s: S[A]): F[S[A]] =
        zipWithIndex(s).traverse { case (a, j) => if(predicate(j)) f(a) else Applicative[F].point(a) }
    }
  }

}