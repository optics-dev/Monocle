package monocle.std

import monocle.function.Index
import monocle.internal.Bits

object boolean extends BooleanOptics

trait BooleanOptics {
  implicit val booleanBitIndex: Index[Boolean, Int, Boolean] =
    Bits.bitsIndex[Boolean]
}