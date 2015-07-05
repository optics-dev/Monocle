package monocle.std

import monocle.Prism
import monocle.function.Index
import monocle.internal.{Bits, Bounded}

import scalaz.std.anyVal._

object long extends LongOptics

trait LongOptics {

  implicit val longBitIndex: Index[Long, Int, Boolean] =
    Bits.bitsIndex[Long]

  val longToInt: Prism[Long, Int]  =
    Bounded.orderingBoundedSafeCast[Long, Int](_.toInt)(_.toLong)

  val longToChar: Prism[Long, Char] =
    Bounded.orderingBoundedSafeCast[Long, Char](_.toChar)(_.toLong)

  val longToByte: Prism[Long, Byte] =
    Bounded.orderingBoundedSafeCast[Long, Byte](_.toByte)(_.toLong)

  val longToBoolean: Prism[Long, Boolean] =
    longToByte composePrism byte.byteToBoolean

}
