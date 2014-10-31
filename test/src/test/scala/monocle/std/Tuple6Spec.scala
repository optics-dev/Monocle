package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.{IsoLaws, LensLaws, TraversalLaws}
import org.specs2.scalaz.Spec


class Tuple6Spec extends Spec {

  checkAll("each tuple6" , TraversalLaws(each[(Int, Int, Int, Int, Int, Int), Int]))

  checkAll("first tuple6" , LensLaws(first[(Int, Char, Boolean, String, Long, Float), Int]))
  checkAll("second tuple6", LensLaws(second[(Int, Char, Boolean, String, Long, Float), Char]))
  checkAll("third tuple6" , LensLaws(third[(Int, Char, Boolean, String, Long, Float), Boolean]))
  checkAll("fourth tuple6", LensLaws(fourth[(Int, Char, Boolean, String, Long, Float), String]))
  checkAll("fifth tuple6" , LensLaws(fifth[(Int, Char, Boolean, String, Long, Float), Long]))
  checkAll("sixth tuple6" , LensLaws(sixth[(Int, Char, Boolean, String, Long, Float), Float]))

  checkAll("hcons tuple6", IsoLaws(cons1[(Int, Char, Boolean, String, Long, Float), Int, (Char, Boolean, String, Long, Float)]))
  checkAll("hsnoc tuple6", IsoLaws(snoc1[(Int, Char, Boolean, String, Long, Float), (Int, Char, Boolean, String, Long), Float]))

}
