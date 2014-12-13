package monocle.std

import monocle.function.Index
import monocle.internal.Bits

object boolean extends BooleanInstances

trait BooleanInstances {
  implicit val booleanBitIndex: Index[Boolean, Int, Boolean] =
    Bits.bitsIndex[Boolean]
}