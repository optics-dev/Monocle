package monocle.function

import monocle.syntax.traversal._
import monocle.{Traversal, SimpleTraversal}
import scalaz.Applicative

trait Tail[S] {

  /** Creates a Traversal between S and the tail of S */
  def tail: SimpleTraversal[S, S]

}

object Tail extends TailInstances

trait TailInstances {

  def tail[S](implicit ev: Tail[S]): SimpleTraversal[S, S] = ev.tail

  implicit def listTail[A] = new Tail[List[A]]{
    def tail: SimpleTraversal[List[A], List[A]] = new Traversal[List[A], List[A], List[A], List[A]] {
      def multiLift[F[_] : Applicative](from: List[A], f: List[A] => F[List[A]]): F[List[A]] = from match {
        case Nil     => Applicative[F].point(Nil)
        case x :: xs => Applicative[F].map(f(xs))(x :: _)
      }
    }
  }

  implicit def streamTail[A] = new Tail[Stream[A]]{
    def tail: SimpleTraversal[Stream[A], Stream[A]] = new Traversal[Stream[A], Stream[A], Stream[A], Stream[A]] {
      def multiLift[F[_] : Applicative](from: Stream[A], f: Stream[A] => F[Stream[A]]): F[Stream[A]] = from match {
        case Stream.Empty => Applicative[F].point(Stream.Empty)
        case x #:: xs     => Applicative[F].map(f(xs))(x #:: _)
      }
    }
  }

  implicit def stringTail: Tail[String] = new Tail[String]{
    import monocle.std.string.stringToList
    def tail: SimpleTraversal[String, String] = stringToList |->> listTail[Char].tail |->> stringToList.reverse
  }

}
