package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{PrismLaws, IsoLaws, LensLaws, TraversalLaws}
import org.specs2.scalaz.Spec

import scalaz.Tree


class TreeSpec extends Spec {

  checkAll("rootLabel", LensLaws(rootLabel[Int]))

  checkAll("subForest", LensLaws(subForest[Int]))

  checkAll("leftMostLabel", LensLaws(leftMostLabel[Int]))

  checkAll("rightMostLabel", LensLaws(rightMostLabel[Int]))


  checkAll("each Tree", TraversalLaws(each[Tree[Int], Int]))

  checkAll("reverse Tree", IsoLaws(reverse[Tree[Int], Tree[Int]]))

}
