package monocle.function

import monocle.LensLaws
import monocle.TestUtil._
import monocle.function.Tail._
import org.specs2.scalaz.Spec


class TailSpec extends Spec {

  checkAll("tail 2-tuple", LensLaws(tail[(Int, Char), Char]))
  checkAll("tail 6-tuple", LensLaws(tail[(Int, Char, Boolean, String, Long, Float), (Char, Boolean, String, Long, Float)]))

}
