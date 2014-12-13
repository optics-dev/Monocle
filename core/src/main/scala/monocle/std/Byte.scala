package monocle.std

import monocle.Prism
import monocle.function.Index
import monocle.internal.{Bits, Bounded}

import scalaz.std.anyVal._

object byte extends ByteInstances

trait ByteInstances {

  implicit val byteBitIndex: Index[Byte, Int, Boolean] =
    Bits.bitsIndex[Byte]

  val byteToBoolean: Prism[Byte, Boolean] =
    Bounded.orderingBoundedSafeCast[Byte, Boolean]{
      case 0 => false
      case 1 => true
    }(if(_) 1 else 0)

}
