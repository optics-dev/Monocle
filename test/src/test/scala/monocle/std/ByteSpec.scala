package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.{LensLaws, PrismLaws}
import org.specs2.scalaz.Spec


class ByteSpec extends Spec {

  checkAll("atBit Byte", LensLaws(atBit[Byte](0)))

  checkAll("Byte to Boolean", PrismLaws(byteToBoolean))

}
