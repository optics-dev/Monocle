package monocle.function

import monocle.Prism

trait EmptyInstancesScalaVersionSpecific {

  /************************************************************************************************/
  /** 2.13 std instances                                                                          */
  /************************************************************************************************/
  implicit def lazyListEmpty[A]: Empty[LazyList[A]] =
    Empty(
      Prism[LazyList[A], Unit](s => if (s.isEmpty) Some(()) else None)(_ => LazyList.empty)
    )
}
