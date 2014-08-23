package monocle.std

import monocle.function._
import monocle.{SimplePrism, Optional, SimpleOptional}
import scalaz.Applicative
import scalaz.std.list._

object list extends ListInstances

trait ListInstances {

  implicit def listReverse[A]: Reverse[List[A], List[A]] =
    Reverse.simple[List[A]](_.reverse)

  implicit def listEach[A]: Each[List[A], A] = Each.traverseEach[List, A]

  implicit def listIndex[A]: Index[List[A], Int, A] =
    Index.traverseIndex[List, A](_.zipWithIndex)

  implicit def listFilterIndex[A]: FilterIndex[List[A], Int, A] =
    FilterIndex.traverseFilterIndex[List, A](_.zipWithIndex)

  implicit def listCons[A]: Cons[List[A], A] = new Cons[List[A], A]{
    def _cons = SimplePrism[List[A], (A, List[A])]({
      case Nil     => None
      case x :: xs => Some(x, xs)
    }, { case (a, s) => a :: s })
  }

  implicit def listSnoc[A]: Snoc[List[A], A] = new Snoc[List[A], A]{
    def _snoc = SimplePrism[List[A], (List[A], A)]( s =>
      for {
        init <- if(s.isEmpty) None else Some(s.init)
        last <- if(s.isEmpty) None else Some(s.last)
      } yield (init, last),
    { case (init, last) => init :+ last }
    )
  }

  implicit def listHeadOption[A]: HeadOption[List[A], A] = new HeadOption[List[A], A] {
    def headOption = SimpleOptional[List[A], A](_.headOption, {
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





}
