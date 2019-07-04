package monocle.function

import monocle.Prism

trait ConsInstancesScalaVersionSpecific {
  /************************************************************************************************/
  /** 2.13 std instances                                                                          */
  /************************************************************************************************/
  implicit def lazyListCons[A]: Cons[LazyList[A], A] = Cons(
    Prism[LazyList[A], (A, LazyList[A])](xs => xs.headOption.map(_ -> xs.tail)){ case (a, s) => a #:: s }
  )
}
