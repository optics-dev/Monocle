package monocle.std

import monocle.function.{AtBit, SafeCast}

object int extends IntInstances

trait IntInstances {

  implicit val intAtBit: AtBit[Int] = AtBit.bitsAtBit[Int]

  implicit val intToChar: SafeCast[Int, Char] = SafeCast.orderingBoundedSafeCast(_.toInt, _.toChar)
  implicit val intToByte: SafeCast[Int, Byte] = SafeCast.orderingBoundedSafeCast(_.toInt, _.toByte)

  implicit val intToBoolean: SafeCast[Int, Boolean] = new SafeCast[Int, Boolean] {
    def safeCast = SafeCast.safeCast[Int, Byte] composePrism SafeCast.safeCast[Byte, Boolean]
  }

}
