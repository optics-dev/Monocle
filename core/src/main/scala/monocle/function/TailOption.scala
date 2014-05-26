package monocle.function

import monocle.{Optional, SimpleOptional}
import scalaz.{INil, IList, ICons, Applicative}

trait TailOption[S, A] {

  /**
   * Creates an Optional between S and its optional tail A
   */
  def tailOption: SimpleOptional[S, A]

}

object TailOption extends TailOptionInstances

trait TailOptionInstances {

  def tailOption[S, A](implicit ev: TailOption[S, A]): SimpleOptional[S, A] = ev.tailOption

  implicit def listTailOption[A] = new TailOption[List[A], List[A]]{
    def tailOption = new Optional[List[A], List[A], List[A], List[A]] {
      def multiLift[F[_] : Applicative](from: List[A], f: List[A] => F[List[A]]): F[List[A]] = from match {
        case Nil     => Applicative[F].point(Nil)
        case x :: xs => Applicative[F].map(f(xs))(x :: _)
      }
    }
  }

  implicit def streamTailOption[A] = new TailOption[Stream[A], Stream[A]]{
    def tailOption = new Optional[Stream[A], Stream[A], Stream[A], Stream[A]] {
      def multiLift[F[_] : Applicative](from: Stream[A], f: Stream[A] => F[Stream[A]]): F[Stream[A]] = from match {
        case Stream.Empty => Applicative[F].point(Stream.Empty)
        case x #:: xs     => Applicative[F].map(f(xs))(x #:: _)
      }
    }
  }

  implicit def vectorTail[A] = new TailOption[Vector[A], Vector[A]]{
    def tailOption = new Optional[Vector[A], Vector[A], Vector[A], Vector[A]] {
      def multiLift[F[_] : Applicative](from: Vector[A], f: Vector[A] => F[Vector[A]]): F[Vector[A]] = from match {
        case Vector() => Applicative[F].point(Vector[A]())
        case x +: xs  => Applicative[F].map(f(xs))(x +: _)
      }
    }
  }

  implicit def IListTailOption[A] = new TailOption[IList[A], IList[A]]{
    def tailOption = new Optional[IList[A], IList[A], IList[A], IList[A]] {
      def multiLift[F[_] : Applicative](from: IList[A], f: IList[A] => F[IList[A]]): F[IList[A]] = from match {
        case INil()  => Applicative[F].point(INil())
        case ICons(x, xs) => Applicative[F].map(f(xs))(x :: _)
      }
    }
  }

  implicit val stringTailOption = new TailOption[String, String]{

    import monocle.std.string.stringToList

    def tailOption = stringToList composeOptional listTailOption[Char].tailOption composeOptional stringToList.reverse
  }

}
