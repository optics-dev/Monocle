package monocle.std

import monocle.function._
import monocle.{SimpleIso, SimpleLens, SimpleOptional, SimplePrism}

import scalaz.{OneAnd, NonEmptyList}
import scalaz.NonEmptyList._
import scalaz.syntax.std.list._
import scalaz.syntax.std.option._

object nonemptylist extends NonEmptyListInstances

trait NonEmptyListInstances{

  implicit def nelEach[A]: Each[NonEmptyList[A], A] = Each.traverseEach[NonEmptyList, A]

  implicit def nelIndex[A]: Index[NonEmptyList[A], Int, A] = new Index[NonEmptyList[A], Int, A] {

    def index(i: Int): SimpleOptional[NonEmptyList[A], A] = i match {
      case 0 => nelHead.head.asOptional
      case _ => nelTail.tail composeOptional list.listIndex.index(i-1)
    }
  }

  implicit def nelFilterIndex[A]: FilterIndex[NonEmptyList[A], Int, A] =
    FilterIndex.traverseFilterIndex[NonEmptyList, A](n => n.zip(NonEmptyList(0, Stream.from(1):_*)))

  implicit def nelReverse[A]: Reverse[NonEmptyList[A], NonEmptyList[A]] =
    reverseFromReverseFunction[NonEmptyList[A]](_.reverse)

  implicit def nelCons[A]: Cons[NonEmptyList[A], A] = new Cons[NonEmptyList[A], A]{
    def _cons = SimplePrism[NonEmptyList[A], (A, NonEmptyList[A])]{ l =>
      l.tail.toNel.toMaybe.map(t => (l.head,t))
    }{ case (a, s) => s.<::(a) }
  }

  implicit def nelSnoc[A]: Snoc[NonEmptyList[A], A] = new Snoc[NonEmptyList[A], A]{

    override def snoc: SimplePrism[NonEmptyList[A], (NonEmptyList[A], A)] =
      SimplePrism(
        (nel: NonEmptyList[A]) =>nel.init.toNel.toMaybe.map(i => (i, nel.last))){
        case (init, last) => init :::> List(last)}

  }

  implicit def nelFieldOne[A]: Field1[NonEmptyList[A], A] = new Field1[NonEmptyList[A],A] {
    override def first: SimpleLens[NonEmptyList[A], A] =
      SimpleLens[NonEmptyList[A], A](_.head)((h: A, list: NonEmptyList[A]) => list.<::(h))
  }

  implicit def nelHead[A]: Head[NonEmptyList[A], A] =  Head.field1Head[NonEmptyList[A],A]

  implicit def nelTail[A] : Tail[NonEmptyList[A], List[A]] = new Tail[NonEmptyList[A], List[A]] {
    override def tail = SimpleLens[NonEmptyList[A], List[A]](_.tail)(
      (tail: List[A], nel: NonEmptyList[A]) => NonEmptyList(nel.head,tail:_*))
  }

  implicit def nelAndOneIso[A] : SimpleIso[NonEmptyList[A], OneAnd[List,A]] =
    SimpleIso((nel: NonEmptyList[A]) => OneAnd[List,A](nel.head,nel.tail))(
              (and: OneAnd[List, A]) => NonEmptyList(and.head,and.tail:_*))


}
