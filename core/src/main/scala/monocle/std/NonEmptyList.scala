package monocle.std

import monocle.function._
import monocle.{PIso, Iso, Optional}

import scalaz.NonEmptyList._
import scalaz.{NonEmptyList, OneAnd}

object nel extends NonEmptyListOptics

trait NonEmptyListOptics {

  final def pNelToOneAnd[A, B]: PIso[NonEmptyList[A], NonEmptyList[B], OneAnd[List,A], OneAnd[List,B]] =
    PIso((nel: NonEmptyList[A])    => OneAnd[List,A](nel.head, nel.tail))(
      (oneAnd: OneAnd[List, B]) => NonEmptyList.nel(oneAnd.head, oneAnd.tail))

  final def nelToOneAnd[A]: Iso[NonEmptyList[A], OneAnd[List,A]] =
    pNelToOneAnd[A, A]

  final def pOptNelToList[A, B]: PIso[Option[NonEmptyList[A]], Option[NonEmptyList[B]], List[A], List[B]] =
    PIso[Option[NonEmptyList[A]], Option[NonEmptyList[B]], List[A], List[B]](_.fold(List.empty[A])(_.list)){
      case Nil     => None
      case x :: xs => Some(NonEmptyList.nel(x, xs))
    }

  final def optNelToList[A]: Iso[Option[NonEmptyList[A]], List[A]] =
    pOptNelToList[A, A]

  @deprecated("use pNelToOneAnd", since = "1.2.0")
  final def pNelAndOneIso[A, B]: PIso[NonEmptyList[A], NonEmptyList[B], OneAnd[List,A], OneAnd[List,B]] =
    pNelToOneAnd[A, B]

  @deprecated("use nelToOneAnd", since = "1.2.0")
  final def nelAndOneIso[A]: Iso[NonEmptyList[A], OneAnd[List,A]] =
    nelToOneAnd[A]


  implicit def nelEach[A]: Each[NonEmptyList[A], A] =
    Each.traverseEach[NonEmptyList, A]

  implicit def nelIndex[A]: Index[NonEmptyList[A], Int, A] =
    new Index[NonEmptyList[A], Int, A] {
      def index(i: Int): Optional[NonEmptyList[A], A] = i match {
        case 0 => nelCons1.head.asOptional
        case _ => nelCons1.tail composeOptional list.listIndex.index(i-1)
      }
    }

  implicit def nelFilterIndex[A]: FilterIndex[NonEmptyList[A], Int, A] =
    FilterIndex.traverseFilterIndex[NonEmptyList, A](_.zipWithIndex)

  implicit def nelReverse[A]: Reverse[NonEmptyList[A], NonEmptyList[A]] =
    Reverse.reverseFromReverseFunction[NonEmptyList[A]](_.reverse)


  implicit def nelCons1[A]: Cons1[NonEmptyList[A], A, List[A]] =
    new Cons1[NonEmptyList[A],A,List[A]]{
      def cons1: Iso[NonEmptyList[A], (A, List[A])] =
        Iso((nel: NonEmptyList[A]) => (nel.head,nel.tail)){case (h,t) => NonEmptyList.nel(h,t)}
    }

  implicit def nelSnoc1[A]:Snoc1[NonEmptyList[A], List[A], A] =
    new Snoc1[NonEmptyList[A],List[A], A]{
      def snoc1: Iso[NonEmptyList[A], (List[A], A)] =
        Iso((nel:NonEmptyList[A]) => nel.init -> nel.last){ case (i,l) => NonEmptyList.nel(l,i.reverse).reverse}
  }


}
