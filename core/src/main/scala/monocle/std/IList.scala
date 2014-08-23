package monocle.std

import scalaz.{IList, Applicative, ICons, INil}
import monocle.function._
import monocle.{SimplePrism, Optional, SimpleOptional}

object ilist extends IListInstances

trait IListInstances {

  implicit def iListEach[A]: Each[IList[A], A] = Each.traverseEach[IList, A]

  implicit def iListIndex[A]: Index[IList[A], Int, A] =
    Index.traverseIndex[IList, A](_.zipWithIndex)

  implicit def iListFilterIndex[A]: FilterIndex[IList[A], Int, A] =
    FilterIndex.traverseFilterIndex[IList, A](_.zipWithIndex)

  implicit def iListCons[A]: Cons[IList[A], A] = new Cons[IList[A], A]{
    def _cons = SimplePrism[IList[A], (A, IList[A])]({
      case INil()       => None
      case ICons(x, xs) => Some(x, xs)
    }, { case (a, s) => ICons(a, s) })
  }

  implicit def iListSnoc[A]: Snoc[IList[A], A] = new Snoc[IList[A], A]{
    def _snoc = SimplePrism[IList[A], (IList[A], A)]( s =>
      for {
        init <- s.initOption
        last <- s.lastOption
      } yield (init, last),
      { case (init, last) => init :+ last }
    )
  }

  implicit def iListHeadOption[A]: HeadOption[IList[A], A] = new HeadOption[IList[A], A] {
    def headOption = SimpleOptional[IList[A], A](_.headOption, {
      case (INil(), a)      => INil[A]()
      case (ICons(x,xs), a) => ICons(a, xs)
    })
  }

  implicit def IListTailOption[A]: TailOption[IList[A], IList[A]] = new TailOption[IList[A], IList[A]]{
    def tailOption = new Optional[IList[A], IList[A], IList[A], IList[A]] {
      def multiLift[F[_] : Applicative](from: IList[A], f: IList[A] => F[IList[A]]): F[IList[A]] = from match {
        case INil()  => Applicative[F].point(INil())
        case ICons(x, xs) => Applicative[F].map(f(xs))(x :: _)
      }
    }
  }

  implicit def iListLastOption[A]: LastOption[IList[A], A]  =
    LastOption.reverseHeadLastOption[IList[A] , A]

  implicit def iListInitOption[A]: InitOption[IList[A], IList[A]] =
    InitOption.reverseTailInitOption[IList[A]]

  implicit def iListReverse[A]: Reverse[IList[A], IList[A]] =
    Reverse.simple[IList[A]](_.reverse)

}
