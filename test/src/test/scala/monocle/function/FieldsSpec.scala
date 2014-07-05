package monocle.function

import monocle.LensLaws
import monocle.TestUtil._
import monocle.function.Fields._
import org.specs2.scalaz.Spec


class FieldsSpec extends Spec {
  
  checkAll("first 2-tuple", LensLaws(first[(Int, Char), Int]))
  checkAll("first 6-tuple", LensLaws(first[(Int, Char, Boolean, String, Long, Float), Int]))

  checkAll("second 2-tuple", LensLaws(second[(Int, Char), Char]))
  checkAll("second 6-tuple", LensLaws(second[(Int, Char, Boolean, String, Long, Float), Char]))

  checkAll("third 3-tuple", LensLaws(third[(Int, Char, Boolean), Boolean]))
  checkAll("third 6-tuple", LensLaws(third[(Int, Char, Boolean, String, Long, Float), Boolean]))

  checkAll("fourth 4-tuple", LensLaws(fourth[(Int, Char, Boolean, String), String]))
  checkAll("fourth 6-tuple", LensLaws(fourth[(Int, Char, Boolean, String, Long, Float), String]))

  checkAll("fifth 5-tuple", LensLaws(fifth[(Int, Char, Boolean, String, Long), Long]))
  checkAll("fifth 6-tuple", LensLaws(fifth[(Int, Char, Boolean, String, Long, Float), Long]))

  checkAll("sixth 6-tuple", LensLaws(sixth[(Int, Char, Boolean, String, Long, Float), Float]))

}
