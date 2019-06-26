package monocle.function

trait ReverseInstancesScalaVersionSpecific {

  /************************************************************************************************/
  /** 2.12 std instances                                                                          */
  /************************************************************************************************/
  implicit def streamReverse[A]: Reverse[Stream[A], Stream[A]] =
    Reverse.fromReverseFunction(_.reverse)
}
