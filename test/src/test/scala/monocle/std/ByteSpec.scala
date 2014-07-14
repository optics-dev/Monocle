package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{PrismLaws, LensLaws}
import org.specs2.scalaz.Spec


class ByteSpec extends Spec {

  checkAll("atBit Byte", LensLaws(atBit[Byte](0)))

  checkAll("safeCast Byte to Boolean", PrismLaws(safeCast[Byte,Boolean]))

}
