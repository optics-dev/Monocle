package monocle.std

import monocle.function.{AtBit, SafeCast}

object byte extends ByteInstances

trait ByteInstances {

  implicit val byteAtBit: AtBit[Byte] = AtBit.bitsAtBit[Byte]

  implicit val byteToBoolean: SafeCast[Byte, Boolean] = SafeCast.orderingBoundedSafeCast(
    if(_) 1 else 0, {
    case 0 => false
    case 1 => true
  })

}
