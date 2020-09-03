package monocle.function

trait FilterIndexInstancesScalaVersionSpecific {

  /************************************************************************************************/
  /** 2.13 std instances                                                                          */
  /************************************************************************************************/
  implicit def lazyListFilterIndex[A]: FilterIndex[LazyList[A], Int, A] =
    FilterIndex.fromTraverse(_.zipWithIndex)
}
