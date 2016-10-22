package monocle.std

import monocle.{Iso, PIso}

import scalaz.IList

object ilist extends IListInstances

trait IListInstances {
  final def pIListToList[A, B]: PIso[IList[A], IList[B], List[A], List[B]] =
    PIso[IList[A], IList[B], List[A], List[B]](_.toList)(IList.fromList)

  final def iListToList[A]: Iso[IList[A], List[A]] =
    pIListToList[A, A]
}
