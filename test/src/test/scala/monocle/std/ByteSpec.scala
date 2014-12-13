package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.{OptionalLaws, PrismLaws}
import org.specs2.scalaz.Spec


class ByteSpec extends Spec {

  checkAll("Byte index bit", OptionalLaws(index[Byte, Int, Boolean](0)))

  checkAll("Byte to Boolean", PrismLaws(byteToBoolean))

}
