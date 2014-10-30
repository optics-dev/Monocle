package monocle.std

import monocle.function.{AtBit, SafeCast}

import scalaz.std.anyVal._

object int extends IntInstances

trait IntInstances {

  implicit val intAtBit: AtBit[Int] = AtBit.bitsAtBit[Int]

  implicit val intToChar: SafeCast[Int, Char] =
    SafeCast.orderingBoundedSafeCast[Int, Char](_.toChar)(_.toInt)

  implicit val intToByte: SafeCast[Int, Byte] =
    SafeCast.orderingBoundedSafeCast[Int, Byte](_.toByte)(_.toInt)

  implicit val intToBoolean: SafeCast[Int, Boolean] = new SafeCast[Int, Boolean] {
    def safeCast = SafeCast.safeCast[Int, Byte] composePrism SafeCast.safeCast[Byte, Boolean]
  }

}
