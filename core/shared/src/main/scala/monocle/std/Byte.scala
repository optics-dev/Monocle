package monocle.std

import monocle.Prism
import monocle.internal.Bounded

import cats.instances.byte._

object byte extends ByteOptics

trait ByteOptics {

  val byteToBoolean: Prism[Byte, Boolean] =
    Bounded.orderingBoundedSafeCast[Byte, Boolean]{
      case 0 => false
      case 1 => true
    }(if(_) 1 else 0)

}
