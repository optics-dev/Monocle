package monocle.std

import monocle.function._
import monocle.{Iso, SimpleOptional, SimplePrism}

import scalaz.Id.Id
import scalaz.syntax.std.option._
import scalaz.syntax.traverse._
import scalaz.{Applicative, ICons, IList, INil, Maybe}

object ilist extends IListInstances

trait IListInstances {

  def iListToList[A, B]: Iso[IList[A], IList[B], List[A], List[B]] =
    Iso[IList[A], IList[B], List[A], List[B]](_.toList)(IList.fromList)

  implicit def iListEmpty[A]: Empty[IList[A]] = new Empty[IList[A]] {
    def empty = SimplePrism[IList[A], Unit](l => if(l.isEmpty) Maybe.just(()) else Maybe.empty)(_ => IList.empty)
  }

  implicit def iNilEmpty[A]: Empty[INil[A]] = new Empty[INil[A]] {
    def empty = SimplePrism[INil[A], Unit](_ => Maybe.just(()))(_ => INil())
  }

  implicit def iListEach[A]: Each[IList[A], A] = Each.traverseEach[IList, A]

  implicit def iListIndex[A]: Index[IList[A], Int, A] = new Index[IList[A], Int, A] {
    def index(i: Int) = SimpleOptional[IList[A], A](
      il      => if(i < 0) Maybe.empty else il.drop(i).headOption.toMaybe)(
      (a, il) => il.zipWithIndex.traverse[Id, A]{
        case (_    , index) if index == i => a
        case (value, index)               => value
      }
    )
  }

  implicit def iListFilterIndex[A]: FilterIndex[IList[A], Int, A] =
    FilterIndex.traverseFilterIndex[IList, A](_.zipWithIndex)

  implicit def iListCons[A]: Cons[IList[A], A] = new Cons[IList[A], A]{
    def cons = SimplePrism[IList[A], (A, IList[A])]{
      case INil()       => Maybe.empty
      case ICons(x, xs) => Maybe.just((x, xs))
    }{ case (a, s) => ICons(a, s) }
  }

  implicit def iListSnoc[A]: Snoc[IList[A], A] = new Snoc[IList[A], A]{
    def snoc = SimplePrism[IList[A], (IList[A], A)](
      il => Applicative[Maybe].apply2(il.initOption.toMaybe, il.lastOption.toMaybe)((_,_))){
      case (init, last) => init :+ last
    }
  }

  implicit def iListReverse[A]: Reverse[IList[A], IList[A]] =
    reverseFromReverseFunction[IList[A]](_.reverse)

}
