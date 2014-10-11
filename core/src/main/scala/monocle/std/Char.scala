package monocle.std

import monocle.function.{AtBit, SafeCast}

import scalaz.Order

object char extends CharInstances

trait CharInstances {

  implicit val charAtBit: AtBit[Char] = AtBit.bitsAtBit[Char]
  implicit val charOrder: Order[Char] = Order.fromScalaOrdering[Char]

  implicit val charToBoolean: SafeCast[Char, Boolean] = SafeCast.orderingBoundedSafeCast[Char, Boolean]{
    case 0 => false
    case 1 => true
  }(if(_) 1 else 0)

}
