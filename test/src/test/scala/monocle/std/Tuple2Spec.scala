package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{IsoLaws, LensLaws, TraversalLaws}
import org.specs2.scalaz.Spec

class Tuple2Spec extends Spec {

  checkAll("each tuple2", TraversalLaws(each[(Int, Int), Int]))

  checkAll("first tuple2", LensLaws(first[(Int, Char), Int]))

  checkAll("second tuple2", LensLaws(second[(Int, Char), Char]))

  checkAll("head tuple2", LensLaws(head[(Int, Char), Int]))

  checkAll("tail tuple2", LensLaws(tail[(Int, Char), Char]))

  checkAll("last tuple2", LensLaws(last[(Int, Char), Char]))

  checkAll("init tuple2", LensLaws(init[(Int, Char), Int]))

  checkAll("reverse tuple2", IsoLaws(_reverse[(Int, Char), (Char, Int)]))

}
