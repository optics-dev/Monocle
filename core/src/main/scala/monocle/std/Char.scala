package monocle.std

import monocle.Prism
import monocle.function.AtBit
import monocle.internal.Bounded

import scalaz.Order

object char extends CharInstances

trait CharInstances {

  implicit val charAtBit: AtBit[Char] =
    AtBit.bitsAtBit[Char]

  implicit val charOrder: Order[Char] =
    Order.fromScalaOrdering[Char]

  val charToBoolean: Prism[Char, Boolean] =
    Bounded.orderingBoundedSafeCast[Char, Boolean]{
      case 0 => false
      case 1 => true
    }(if(_) 1 else 0)

}
