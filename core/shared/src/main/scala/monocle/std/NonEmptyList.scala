package monocle.std

import monocle.{Iso, PIso}

import cats.data.{NonEmptyList, OneAnd}

object nel extends NonEmptyListOptics

trait NonEmptyListOptics {
  final def pNelToOneAnd[A, B]: PIso[NonEmptyList[A], NonEmptyList[B], OneAnd[List, A], OneAnd[List, B]] =
    PIso((nel: NonEmptyList[A]) => OneAnd[List, A](nel.head, nel.tail))((oneAnd: OneAnd[List, B]) =>
      NonEmptyList(oneAnd.head, oneAnd.tail)
    )

  final def nelToOneAnd[A]: Iso[NonEmptyList[A], OneAnd[List, A]] =
    pNelToOneAnd[A, A]

  final def pOptNelToList[A, B]: PIso[Option[NonEmptyList[A]], Option[NonEmptyList[B]], List[A], List[B]] =
    PIso[Option[NonEmptyList[A]], Option[NonEmptyList[B]], List[A], List[B]](_.fold(List.empty[A])(_.toList))(
      NonEmptyList.fromList
    )

  final def optNelToList[A]: Iso[Option[NonEmptyList[A]], List[A]] =
    pOptNelToList[A, A]
}
