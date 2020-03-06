package monocle.std

import monocle.Prism
import monocle.internal.Bounded

import cats.instances.long._

object long extends LongOptics

trait LongOptics {
  val longToInt: Prism[Long, Int] =
    Bounded.orderingBoundedSafeCast[Long, Int](_.toInt)(_.toLong)

  val longToChar: Prism[Long, Char] =
    Bounded.orderingBoundedSafeCast[Long, Char](_.toChar)(_.toLong)

  val longToByte: Prism[Long, Byte] =
    Bounded.orderingBoundedSafeCast[Long, Byte](_.toByte)(_.toLong)

  val longToBoolean: Prism[Long, Boolean] =
    longToByte composePrism byte.byteToBoolean
}
