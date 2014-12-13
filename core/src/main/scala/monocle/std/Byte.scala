package monocle.std

import monocle.Prism
import monocle.function.AtBit
import monocle.internal.Bounded

import scalaz.std.anyVal._

object byte extends ByteInstances

trait ByteInstances {

  implicit val byteAtBit: AtBit[Byte] =
    AtBit.bitsAtBit[Byte]

  val byteToBoolean: Prism[Byte, Boolean] =
    Bounded.orderingBoundedSafeCast[Byte, Boolean]{
      case 0 => false
      case 1 => true
    }(if(_) 1 else 0)

}
