package monocle.function

import org.specs2.scalaz.Spec
import monocle.LensLaws
import monocle.function.Init._

class InitSpec extends Spec {

  checkAll("init 2-tuple", LensLaws(init[(Int, Char), Int]))
  checkAll("init 6-tuple", LensLaws(init[(Int, Char, Boolean, String, Long, Float), (Int, Char, Boolean, String, Long)]))


}
