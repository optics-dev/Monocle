package monocle.std

import monocle.SimplePrism
import monocle.function._

import scalaz.{Maybe, ICons, IList, INil}

object ilist extends IListInstances

trait IListInstances {

  implicit def iListEmpty[A]: Empty[IList[A]] = new Empty[IList[A]] {
    def empty = SimplePrism[IList[A], Unit](l => if(l.isEmpty) Maybe.just(()) else Maybe.empty, _ => IList.empty)
  }

  implicit def iNilEmpty[A]: Empty[INil[A]] = new Empty[INil[A]] {
    def empty = SimplePrism[INil[A], Unit](_ => Maybe.just(()), _ => INil())
  }

  implicit def iListEach[A]: Each[IList[A], A] = Each.traverseEach[IList, A]

  implicit def iListIndex[A]: Index[IList[A], Int, A] =
    Index.traverseIndex[IList, A](_.zipWithIndex)

  implicit def iListFilterIndex[A]: FilterIndex[IList[A], Int, A] =
    FilterIndex.traverseFilterIndex[IList, A](_.zipWithIndex)

  implicit def iListCons[A]: Cons[IList[A], A] = new Cons[IList[A], A]{
    def _cons = SimplePrism[IList[A], (A, IList[A])]({
      case INil()       => Maybe.empty
      case ICons(x, xs) => Maybe.just(x, xs)
    }, { case (a, s) => ICons(a, s) })
  }

  implicit def iListSnoc[A]: Snoc[IList[A], A] = new Snoc[IList[A], A]{
    def snoc = SimplePrism[IList[A], (IList[A], A)]( s => Maybe.optionMaybeIso.to(
      for {
        init <- s.initOption
        last <- s.lastOption
      } yield (init, last)),
      { case (init, last) => init :+ last }
    )
  }

  implicit def iListHeadOption[A]: HeadOption[IList[A], A] =
    HeadOption.consHeadOption[IList[A], A]

  implicit def IListTailOption[A]: TailOption[IList[A], IList[A]] =
    TailOption.consTailOption[IList[A], A]

  implicit def iListLastOption[A]: LastOption[IList[A], A]  =
    LastOption.snocLastOption[IList[A] , A]

  implicit def iListInitOption[A]: InitOption[IList[A], IList[A]] =
    InitOption.snocInitOption[IList[A], A]

  implicit def iListReverse[A]: Reverse[IList[A], IList[A]] =
    reverseFromReverseFunction[IList[A]](_.reverse)

}
