package monocle.std

import monocle.Prism
import monocle.internal.Bounded

object int extends IntOptics

trait IntOptics {
  val intToChar: Prism[Int, Char] =
    Bounded.orderingBoundedSafeCast[Int, Char](_.toChar)(_.toInt)

  val intToByte: Prism[Int, Byte] =
    Bounded.orderingBoundedSafeCast[Int, Byte](_.toByte)(_.toInt)

  val intToBoolean: Prism[Int, Boolean] =
    intToByte composePrism byte.byteToBoolean
}
