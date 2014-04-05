package monocle.util

import monocle.{Traversal, SimpleTraversal}
import scalaz.Applicative

trait Index[S, I, A] {

  /** Creates a Traversal from S to 0 or 1 A */
  def index(i: I): SimpleTraversal[S, A]

}

object Index {

  def index[S, I, A](i: I)(implicit ev: Index[S, I, A]): SimpleTraversal[S, A] = ev.index(i)

  implicit def indexedMap[K, V] = new Index[Map[K, V], K, V] {
    def index(i: K): SimpleTraversal[Map[K, V], V] = {
      import monocle.syntax.traversal._
      monocle.std.map.at[K, V](i) |->> monocle.std.option.some
    }

  }

  implicit def indexList[A] = new Index[List[A], Int, A] {
    def index(i: Int): SimpleTraversal[List[A], A] = new Traversal[List[A], List[A], A, A]{
      def multiLift[F[_] : Applicative](from: List[A], f: A => F[A]): F[List[A]] = {
        split(from, i).map{ case (before, a, after) =>
          Applicative[F].map(f(a))(newA => before ++ (newA :: after))
        }.getOrElse(Applicative[F].point(from))
      }

      private def split(list: List[A], i: Int): Option[(List[A], A, List[A])] = {
        list.splitAt(i) match {
          case (before, a :: after) => Some((before, a, after))
          case _                    => None
        }

      }
    }
  }


}
