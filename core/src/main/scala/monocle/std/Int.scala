package monocle.std

import monocle.Prism
import monocle.function.AtBit
import monocle.internal.Bounded

import scalaz.std.anyVal._

object int extends IntInstances

trait IntInstances {

  implicit val intAtBit: AtBit[Int] =
    AtBit.bitsAtBit[Int]

  val intToChar: Prism[Int, Char] =
    Bounded.orderingBoundedSafeCast[Int, Char](_.toChar)(_.toInt)

  val intToByte: Prism[Int, Byte] =
    Bounded.orderingBoundedSafeCast[Int, Byte](_.toByte)(_.toInt)

  val intToBoolean: Prism[Int, Boolean] =
    intToByte composePrism byte.byteToBoolean


}
