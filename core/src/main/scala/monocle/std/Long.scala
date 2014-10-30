package monocle.std

import monocle.function.{AtBit, SafeCast}

import scalaz.std.anyVal._

object long extends LongInstances

trait LongInstances {

  implicit val longAtBit: AtBit[Long] = AtBit.bitsAtBit[Long]

  implicit val longToInt : SafeCast[Long, Int]  =
    SafeCast.orderingBoundedSafeCast[Long, Int](_.toInt)(_.toLong)

  implicit val longToChar: SafeCast[Long, Char] =
    SafeCast.orderingBoundedSafeCast[Long, Char](_.toChar)(_.toInt)

  implicit val longToByte: SafeCast[Long, Byte] =
    SafeCast.orderingBoundedSafeCast[Long, Byte](_.toByte)(_.toLong)

  implicit val longToBoolean: SafeCast[Long, Boolean] = new SafeCast[Long, Boolean] {
    def safeCast = SafeCast.safeCast[Long, Byte] composePrism SafeCast.safeCast[Byte, Boolean]
  }

}
