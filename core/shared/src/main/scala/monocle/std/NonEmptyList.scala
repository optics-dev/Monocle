package monocle.std

import monocle.{Iso, PIso}

import scalaz.{ICons, IList, INil, NonEmptyList, OneAnd}

object nel extends NonEmptyListOptics

trait NonEmptyListOptics {

  final def pNelToOneAnd[A, B]: PIso[NonEmptyList[A], NonEmptyList[B], OneAnd[List,A], OneAnd[List,B]] =
    PIso((nel: NonEmptyList[A])    => OneAnd[List,A](nel.head, nel.tail.toList))(
      (oneAnd: OneAnd[List, B]) => NonEmptyList.nel(oneAnd.head, IList.fromList(oneAnd.tail)))

  final def nelToOneAnd[A]: Iso[NonEmptyList[A], OneAnd[List,A]] =
    pNelToOneAnd[A, A]

  final def pOptNelToList[A, B]: PIso[Option[NonEmptyList[A]], Option[NonEmptyList[B]], List[A], List[B]] =
    PIso[Option[NonEmptyList[A]], Option[NonEmptyList[B]], IList[A], IList[B]](_.fold(IList.empty[A])(_.list)){
      case INil()       => None
      case ICons(x, xs) => Some(NonEmptyList.nel(x, xs))
    } composeIso ilist.pIListToList

  final def optNelToList[A]: Iso[Option[NonEmptyList[A]], List[A]] =
    pOptNelToList[A, A]

  @deprecated("use pNelToOneAnd", since = "1.2.0")
  final def pNelAndOneIso[A, B]: PIso[NonEmptyList[A], NonEmptyList[B], OneAnd[List,A], OneAnd[List,B]] =
    pNelToOneAnd[A, B]

  @deprecated("use nelToOneAnd", since = "1.2.0")
  final def nelAndOneIso[A]: Iso[NonEmptyList[A], OneAnd[List,A]] =
    nelToOneAnd[A]
}
