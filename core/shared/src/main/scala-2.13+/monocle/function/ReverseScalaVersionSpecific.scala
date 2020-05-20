package monocle.function

trait ReverseInstancesScalaVersionSpecific {

  /************************************************************************************************/
  /** 2.13 std instances                                                                          */
  /************************************************************************************************/
  implicit def lazyListReverse[A]: Reverse[LazyList[A], LazyList[A]] =
    Reverse.fromReverseFunction(_.reverse)
}
