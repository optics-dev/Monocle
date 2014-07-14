package monocle.std

import monocle.function.{AtBit, SafeCast}

object char extends CharInstances

trait CharInstances {

  implicit val charAtBit: AtBit[Char] = AtBit.bitsAtBit[Char]

  implicit val charToBoolean: SafeCast[Char, Boolean] = SafeCast.orderingBoundedSafeCast(
    if(_) 1 else 0, {
    case 0 => false
    case 1 => true
  })

}
