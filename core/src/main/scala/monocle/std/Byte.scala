package monocle.std

import monocle.function.{AtBit, SafeCast}
import scalaz.std.anyVal._

object byte extends ByteInstances

trait ByteInstances {

  implicit val byteAtBit: AtBit[Byte] = AtBit.bitsAtBit[Byte]

  implicit val byteToBoolean: SafeCast[Byte, Boolean] = SafeCast.orderingBoundedSafeCast[Byte, Boolean]{
      case 0 => false
      case 1 => true
    }(if(_) 1 else 0)

}
