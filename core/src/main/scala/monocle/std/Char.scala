package monocle.std

import monocle.Prism
import monocle.function.Index
import monocle.internal.{Bits, Bounded}

import scalaz.Order

object char extends CharOptics

trait CharOptics {

  implicit val charBitIndex: Index[Char, Int, Boolean] =
    Bits.bitsIndex[Char]

  implicit val charOrder: Order[Char] =
    Order.fromScalaOrdering[Char]

  val charToBoolean: Prism[Char, Boolean] =
    Bounded.orderingBoundedSafeCast[Char, Boolean]{
      case 0 => false
      case 1 => true
    }(if(_) 1 else 0)

}
