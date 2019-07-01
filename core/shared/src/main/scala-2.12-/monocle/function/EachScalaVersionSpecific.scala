package monocle.function

import cats.instances.stream._

trait EachInstancesScalaVersionSpecific {

  /************************************************************************************************/
  /** 2.12 std instances                                                                          */
  /************************************************************************************************/
  implicit def streamEach[A]: Each[Stream[A], A] = Each.fromTraverse
}
