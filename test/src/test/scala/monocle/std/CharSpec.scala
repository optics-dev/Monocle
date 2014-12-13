package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.{LensLaws, PrismLaws}
import org.specs2.scalaz.Spec

class CharSpec extends Spec {

  checkAll("atBit Char", LensLaws(atBit[Char](0)))

  checkAll("Char to Boolean ", PrismLaws(charToBoolean))

}
