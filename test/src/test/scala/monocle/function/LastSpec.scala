package monocle.function

import monocle.LensLaws
import monocle.TestUtil._
import monocle.function.Last._
import org.specs2.scalaz.Spec

class LastSpec extends Spec {

  checkAll("last 2-tuple", LensLaws(last[(Int, Char), Char]))
  checkAll("last 6-tuple", LensLaws(last[(Int, Char, Boolean, String, Long, Float), Float]))

}