package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.{LensLaws, TraversalLaws}
import org.specs2.scalaz.Spec


class Tuple6Spec extends Spec {

  checkAll("each tuple6" , TraversalLaws(each[(Int, Int, Int, Int, Int, Int), Int]))

  checkAll("first tuple6" , LensLaws(first[(Int, Char, Boolean, String, Long, Float), Int]))
  checkAll("second tuple6", LensLaws(second[(Int, Char, Boolean, String, Long, Float), Char]))
  checkAll("third tuple6" , LensLaws(third[(Int, Char, Boolean, String, Long, Float), Boolean]))
  checkAll("fourth tuple6", LensLaws(fourth[(Int, Char, Boolean, String, Long, Float), String]))
  checkAll("fifth tuple6" , LensLaws(fifth[(Int, Char, Boolean, String, Long, Float), Long]))
  checkAll("sixth tuple6" , LensLaws(sixth[(Int, Char, Boolean, String, Long, Float), Float]))

  checkAll("head tuple6", LensLaws(head[(Int, Char, Boolean, String, Long, Float), Int]))

  checkAll("tail tuple6", LensLaws(tail[(Int, Char, Boolean, String, Long, Float), (Char, Boolean, String, Long, Float)]))

  checkAll("last tuple6", LensLaws(last[(Int, Char, Boolean, String, Long, Float), Float]))

  checkAll("init tuple6", LensLaws(init[(Int, Char, Boolean, String, Long, Float), (Int, Char, Boolean, String, Long)]))


}
