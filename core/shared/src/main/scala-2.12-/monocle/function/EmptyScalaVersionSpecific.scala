package monocle.function

import monocle.Prism

trait EmptyInstancesScalaVersionSpecific {

  /************************************************************************************************/
  /** 2.12 std instances                                                                          */
  /************************************************************************************************/
  implicit def streamEmpty[A]: Empty[Stream[A]] = Empty(
    Prism[Stream[A], Unit](s => if(s.isEmpty) Some(()) else None)(_ => Stream.empty)
  )
}
