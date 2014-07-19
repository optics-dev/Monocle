package monocle.std

import monocle.function._
import monocle.{Optional, SimpleOptional}
import scalaz.Applicative
import scalaz.std.list._

object list extends ListInstances

trait ListInstances {

  implicit def listEach[A]: Each[List[A], A] = Each.traverseEach[List, A]

  implicit def listIndex[A]: Index[List[A], Int, A] =
    Index.traverseIndex[List, A](_.zipWithIndex)

  implicit def listFilterIndex[A]: FilterIndex[List[A], Int, A] =
    FilterIndex.traverseFilterIndex[List, A](_.zipWithIndex)

  implicit def listHeadOption[A]: HeadOption[List[A], A] = new HeadOption[List[A], A] {
    def headOption = SimpleOptional.build[List[A], A](_.headOption, {
      case (Nil, a)     => Nil
      case (x :: xs, a) => a :: xs
    })
  }

  implicit def listTailOption[A]: TailOption[List[A], List[A]] = new TailOption[List[A], List[A]]{
    def tailOption = new Optional[List[A], List[A], List[A], List[A]] {
      def multiLift[F[_] : Applicative](from: List[A], f: List[A] => F[List[A]]): F[List[A]] = from match {
        case Nil     => Applicative[F].point(Nil)
        case x :: xs => Applicative[F].map(f(xs))(x :: _)
      }
    }
  }

  implicit def listLastOption[A]: LastOption[List[A], A] =
    LastOption.reverseHeadLastOption[List[A]  , A]

  implicit def listInitOption[A]: InitOption[List[A], List[A]] =
    InitOption.reverseTailInitOption[List[A]]

  implicit def listReverse[A]: Reverse[List[A], List[A]] =
    Reverse.simple[List[A]](_.reverse)



}
