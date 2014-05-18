package monocle.function

import monocle.LensLaws
import monocle.TestUtil._
import monocle.function.Fields._
import org.specs2.scalaz.Spec


class FieldsSpec extends Spec {
  
  checkAll("_1 2-tuple", LensLaws(_1[(Int, Char), Int]))
  checkAll("_1 6-tuple", LensLaws(_1[(Int, Char, Boolean, String, Long, Float), Int]))

  checkAll("_2 2-tuple", LensLaws(_2[(Int, Char), Char]))
  checkAll("_2 6-tuple", LensLaws(_2[(Int, Char, Boolean, String, Long, Float), Char]))

  checkAll("_3 3-tuple", LensLaws(_3[(Int, Char, Boolean), Boolean]))
  checkAll("_3 6-tuple", LensLaws(_3[(Int, Char, Boolean, String, Long, Float), Boolean]))

  checkAll("_4 4-tuple", LensLaws(_4[(Int, Char, Boolean, String), String]))
  checkAll("_4 6-tuple", LensLaws(_4[(Int, Char, Boolean, String, Long, Float), String]))

  checkAll("_5 5-tuple", LensLaws(_5[(Int, Char, Boolean, String, Long), Long]))
  checkAll("_5 6-tuple", LensLaws(_5[(Int, Char, Boolean, String, Long, Float), Long]))

  checkAll("_6 6-tuple", LensLaws(_6[(Int, Char, Boolean, String, Long, Float), Float]))

}
