package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.{TraversalLaws, LensLaws, IsoLaws}
import org.specs2.scalaz.Spec

class Tuple2Spec extends Spec {

  checkAll("each tuple2", TraversalLaws(each[(Int, Int), Int]))

  checkAll("first tuple2", LensLaws(first[(Int, Char), Int]))
  checkAll("second tuple2", LensLaws(second[(Int, Char), Char]))

  checkAll("hcons tuple2", IsoLaws(cons1[(Int, Char), Int, Char]))
  checkAll("hsnoc tuple2", IsoLaws(snoc1[(Int, Char), Int, Char]))

  checkAll("reverse tuple2", IsoLaws(reverse[(Int, Char), (Char, Int)]))

}
