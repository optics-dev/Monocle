package monocle.function

import monocle.{Optional, SimpleOptional}
import scalaz.{INil, IList, ICons, Applicative}

trait Tail[S, A] {

  /**
   * Creates an Optional between S and its optional tail A
   */
  def tail: SimpleOptional[S, A]

}

object Tail extends TailInstances

trait TailInstances {

  def tail[S, A](implicit ev: Tail[S, A]): SimpleOptional[S, A] = ev.tail

  implicit def listTail[A] = new Tail[List[A], List[A]]{
    def tail = new Optional[List[A], List[A], List[A], List[A]] {
      def multiLift[F[_] : Applicative](from: List[A], f: List[A] => F[List[A]]): F[List[A]] = from match {
        case Nil     => Applicative[F].point(Nil)
        case x :: xs => Applicative[F].map(f(xs))(x :: _)
      }
    }
  }

  implicit def streamTail[A] = new Tail[Stream[A], Stream[A]]{
    def tail = new Optional[Stream[A], Stream[A], Stream[A], Stream[A]] {
      def multiLift[F[_] : Applicative](from: Stream[A], f: Stream[A] => F[Stream[A]]): F[Stream[A]] = from match {
        case Stream.Empty => Applicative[F].point(Stream.Empty)
        case x #:: xs     => Applicative[F].map(f(xs))(x #:: _)
      }
    }
  }

  implicit def vectorTail[A] = new Tail[Vector[A], Vector[A]]{
    def tail = new Optional[Vector[A], Vector[A], Vector[A], Vector[A]] {
      def multiLift[F[_] : Applicative](from: Vector[A], f: Vector[A] => F[Vector[A]]): F[Vector[A]] = from match {
        case Vector() => Applicative[F].point(Vector[A]())
        case x +: xs  => Applicative[F].map(f(xs))(x +: _)
      }
    }
  }

  implicit def IListTail[A] = new Tail[IList[A], IList[A]]{
    def tail = new Optional[IList[A], IList[A], IList[A], IList[A]] {
      def multiLift[F[_] : Applicative](from: IList[A], f: IList[A] => F[IList[A]]): F[IList[A]] = from match {
        case INil()  => Applicative[F].point(INil())
        case ICons(x, xs) => Applicative[F].map(f(xs))(x :: _)
      }
    }
  }

  implicit val stringTail = new Tail[String, String]{

    import monocle.std.string.stringToList

    def tail = stringToList composeOptional listTail[Char].tail composeOptional stringToList.reverse
  }

}
