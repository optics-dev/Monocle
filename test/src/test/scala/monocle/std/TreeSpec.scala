package monocle.std

import monocle.MonocleSuite
import monocle.function.Plated._
import monocle.law.discipline.{LensTests, TraversalTests}
import monocle.law.discipline.function.{EachTests, ReverseTests}

import scalaz.Tree

class TreeSpec extends MonocleSuite {
  checkAll("rootLabel", LensTests(rootLabel[Int]))
  checkAll("subForest", LensTests(subForest[Int]))
  checkAll("leftMostLabel", LensTests(leftMostLabel[Int]))
  checkAll("rightMostLabel", LensTests(rightMostLabel[Int]))

  checkAll("each Tree", EachTests[Tree[Int], Int])
  checkAll("reverse Tree", ReverseTests[Tree[Int], Tree[Int]])

  checkAll("plated Tree", TraversalTests(plate[Tree[Int]]))
}
