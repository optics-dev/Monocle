package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.LensLaws
import org.specs2.scalaz.Spec

class BooleanSpec extends Spec {

  checkAll("atBit Boolean", LensLaws(atBit[Boolean](0)))

}
