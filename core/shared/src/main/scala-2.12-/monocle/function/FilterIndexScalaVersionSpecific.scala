package monocle.function

import cats.instances.stream._

trait FilterIndexInstancesScalaVersionSpecific {

  /************************************************************************************************/
  /** 2.12 std instances                                                                          */
  /************************************************************************************************/
  implicit def streamFilterIndex[A]: FilterIndex[Stream[A], Int, A] =
    FilterIndex.fromTraverse(_.zipWithIndex)
}
