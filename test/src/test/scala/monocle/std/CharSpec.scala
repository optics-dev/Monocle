package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.{OptionalLaws, PrismLaws}
import org.specs2.scalaz.Spec

class CharSpec extends Spec {

  checkAll("Char index bit", OptionalLaws(index[Char, Int, Boolean](0)))

  checkAll("Char to Boolean ", PrismLaws(charToBoolean))

}
