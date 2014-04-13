package monocle.function

import monocle.Lens
import monocle.TestUtil._
import monocle.function.Fields._
import org.specs2.scalaz.Spec


class FieldsSpec extends Spec {
  
  checkAll("_1 2-tuple", Lens.laws(_1[(Int, Char), Int]))
  checkAll("_1 3-tuple", Lens.laws(_1[(Int, Char, Boolean), Int]))
  checkAll("_1 4-tuple", Lens.laws(_1[(Int, Char, Boolean, String), Int]))
  checkAll("_1 5-tuple", Lens.laws(_1[(Int, Char, Boolean, String, Long), Int]))
  checkAll("_1 6-tuple", Lens.laws(_1[(Int, Char, Boolean, String, Long, Float), Int]))

  checkAll("_2 2-tuple", Lens.laws(_2[(Int, Char), Char]))
  checkAll("_2 3-tuple", Lens.laws(_2[(Int, Char, Boolean), Char]))
  checkAll("_2 4-tuple", Lens.laws(_2[(Int, Char, Boolean, String), Char]))
  checkAll("_2 5-tuple", Lens.laws(_2[(Int, Char, Boolean, String, Long), Char]))
  checkAll("_2 6-tuple", Lens.laws(_2[(Int, Char, Boolean, String, Long, Float), Char]))

  checkAll("_3 3-tuple", Lens.laws(_3[(Int, Char, Boolean), Boolean]))
  checkAll("_3 4-tuple", Lens.laws(_3[(Int, Char, Boolean, String), Boolean]))
  checkAll("_3 5-tuple", Lens.laws(_3[(Int, Char, Boolean, String, Long), Boolean]))
  checkAll("_3 6-tuple", Lens.laws(_3[(Int, Char, Boolean, String, Long, Float), Boolean]))

  checkAll("_4 4-tuple", Lens.laws(_4[(Int, Char, Boolean, String), String]))
  checkAll("_4 5-tuple", Lens.laws(_4[(Int, Char, Boolean, String, Long), String]))
  checkAll("_4 6-tuple", Lens.laws(_4[(Int, Char, Boolean, String, Long, Float), String]))

  checkAll("_5 5-tuple", Lens.laws(_5[(Int, Char, Boolean, String, Long), Long]))
  checkAll("_5 6-tuple", Lens.laws(_5[(Int, Char, Boolean, String, Long, Float), Long]))

  checkAll("_6 6-tuple", Lens.laws(_6[(Int, Char, Boolean, String, Long, Float), Float]))

}
