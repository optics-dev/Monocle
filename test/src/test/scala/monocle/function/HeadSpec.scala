package monocle.function

import monocle.LensLaws
import monocle.TestUtil._
import monocle.function.Head._
import org.specs2.scalaz.Spec

class HeadSpec extends Spec {

  checkAll("head 2-tuple", LensLaws(head[(Int, Char), Int]))
  checkAll("head 6-tuple", LensLaws(head[(Int, Char, Boolean, String, Long, Float), Int]))

}