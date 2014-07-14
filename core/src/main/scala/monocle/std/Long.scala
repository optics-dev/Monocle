package monocle.std

import monocle.function.{AtBit, SafeCast}

object long extends LongInstances

trait LongInstances {

  implicit val longAtBit: AtBit[Long] = AtBit.bitsAtBit[Long]

  implicit val longToInt : SafeCast[Long, Int]  = SafeCast.orderingBoundedSafeCast(_.toLong, _.toInt)
  implicit val longToChar: SafeCast[Long, Char] = SafeCast.orderingBoundedSafeCast(_.toInt , _.toChar)
  implicit val longToByte: SafeCast[Long, Byte] = SafeCast.orderingBoundedSafeCast(_.toLong, _.toByte)

  implicit val longToBoolean: SafeCast[Long, Boolean] = new SafeCast[Long, Boolean] {
    def safeCast = SafeCast.safeCast[Long, Byte] composePrism SafeCast.safeCast[Byte, Boolean]
  }

}
