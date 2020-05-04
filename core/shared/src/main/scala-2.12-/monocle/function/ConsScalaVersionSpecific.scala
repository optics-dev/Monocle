package monocle.function

import monocle.Prism
import scala.collection.immutable.Stream.#::

trait ConsInstancesScalaVersionSpecific {

  /************************************************************************************************/
  /** 2.12 std instances                                                                          */
  /************************************************************************************************/
  implicit def streamCons[A]: Cons[Stream[A], A] =
    Cons(
      Prism[Stream[A], (A, Stream[A])] {
        case scala.collection.immutable.Stream.Empty => None
        case x #:: xs                                => Some((x, xs))
      } { case (a, s) => a #:: s }
    )

}
