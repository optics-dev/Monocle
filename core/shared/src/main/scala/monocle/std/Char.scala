package monocle.std

import monocle.Prism
import monocle.internal.Bounded

import cats.instances.char._

object char extends CharOptics

trait CharOptics {

  val charToBoolean: Prism[Char, Boolean] =
    Bounded.orderingBoundedSafeCast[Char, Boolean]{
      case 0 => false
      case 1 => true
    }(if(_) 1 else 0)

}
