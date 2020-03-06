package monocle.function

import cats.instances.lazyList._

trait EachInstancesScalaVersionSpecific {

  /************************************************************************************************/
  /** 2.13 std instances                                                                          */
  /************************************************************************************************/
  implicit def lazyListEach[A]: Each[LazyList[A], A] = Each.fromTraverse[LazyList, A]
}
