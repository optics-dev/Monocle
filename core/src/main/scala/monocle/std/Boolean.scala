package monocle.std

import monocle.function.AtBit


object boolean extends BooleanInstances

trait BooleanInstances {
  implicit val booleanAtBit: AtBit[Boolean] = AtBit.bitsAtBit[Boolean]
}