package monocle.function

import monocle.Optional

trait IndexInstancesScalaVersionSpecific {

  /************************************************************************************************/
  /** 2.13 std instances                                                                          */
  /************************************************************************************************/
  implicit def lazyListIndex[A]: Index[LazyList[A], Int, A] =
    Index(i =>
      if (i < 0) Optional.void
      else
        Optional[LazyList[A], A](_.drop(i).headOption)(a =>
          s => s.zipWithIndex.map { case (value, index) => if (i == index) a else value }
        )
    )
}
