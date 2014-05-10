package monocle.function

import monocle.syntax.traversal._
import monocle.{Traversal, SimpleTraversal}
import scalaz.{INil, IList, ICons, Applicative}

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

  implicit def vectorTail[A] = new Tail[Vector[A]]{
    def tail: SimpleTraversal[Vector[A], Vector[A]] = new Traversal[Vector[A], Vector[A], Vector[A], Vector[A]] {
      def multiLift[F[_] : Applicative](from: Vector[A], f: Vector[A] => F[Vector[A]]): F[Vector[A]] = from match {
        case Vector() => Applicative[F].point(Vector[A]())
        case x +: xs  => Applicative[F].map(f(xs))(x +: _)
      }
    }
  }

  implicit def IListTail[A] = new Tail[IList[A]]{
    def tail: SimpleTraversal[IList[A], IList[A]] = new Traversal[IList[A], IList[A], IList[A], IList[A]] {
      def multiLift[F[_] : Applicative](from: IList[A], f: IList[A] => F[IList[A]]): F[IList[A]] = from match {
        case INil()  => Applicative[F].point(INil())
        case ICons(x, xs) => Applicative[F].map(f(xs))(x :: _)
      }
    }
  }

}
