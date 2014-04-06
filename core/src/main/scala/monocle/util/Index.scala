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

  implicit def indexWithSplit[S, I, A](implicit ev: Split[S, I, A]): Index[S, I, A] = new Index[S, I, A]{
    def index(i: I): SimpleTraversal[S, A] = new Traversal[S, S, A, A] {
      def multiLift[F[_] : Applicative](from: S, f: (A) => F[A]): F[S] =
        ev.split(from, i).map{ case (before, a, after) =>
          Applicative[F].map(f(a))(newA => ev.merge(before, newA, after))
        }.getOrElse(Applicative[F].point(from))
    }

  }

  /** Split is type class to facilitate creation of Index*/
  trait Split[S, I, A]{
    /**
     * Split an S at index
     * @return if S has no value at index then None, otherwise returns all values before index,
     *         the value at the index and all values after index
     */
    def split(from: S, index: I): Option[(S, A, S)]

    def merge(before: S, at: A, after: S): S

  }

  implicit def listSplit[A] = new Split[List[A], Int, A] {
    def merge(before: List[A], at: A, after: List[A]): List[A] = before ::: at :: after

    def split(from: List[A], index: Int): Option[(List[A], A, List[A])] =
      from.splitAt(index) match {
        case (before, at :: after) => Some(before, at, after)
        case _                     => None
      }
  }

  implicit val stringSplit = new Split[String, Int, Char] {
    def merge(before: String, at: Char, after: String): String = before + at + after
    def split(from: String, index: Int): Option[(String, Char, String)] = {
      val (before, atAndAfter) = from.splitAt(index)
      val (at, after)          = atAndAfter.splitAt(1)
      at.headOption.map((before, _, after))
    }
  }


}


