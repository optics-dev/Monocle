package monocle.std

import monocle.Prism
import monocle.function.Index
import monocle.internal.{Bits, Bounded}

import scalaz.std.anyVal._

object int extends IntInstances

trait IntInstances {

  implicit val intBitIndex: Index[Int, Int, Boolean] =
    Bits.bitsIndex[Int]

  val intToChar: Prism[Int, Char] =
    Bounded.orderingBoundedSafeCast[Int, Char](_.toChar)(_.toInt)

  val intToByte: Prism[Int, Byte] =
    Bounded.orderingBoundedSafeCast[Int, Byte](_.toByte)(_.toInt)

  val intToBoolean: Prism[Int, Boolean] =
    intToByte composePrism byte.byteToBoolean


}
