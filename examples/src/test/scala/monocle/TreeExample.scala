package monocle

import monocle.thirdparty.tree._
import org.specs2.scalaz.{ScalazMatchers, Spec}
import scalaz.Tree._
import monocle.TestUtil._
import monocle.syntax.lens._

class TreeExample extends Spec with ScalazMatchers {

  //     1
  //    / \
  //   2   3
  val tree = node(1, Stream(leaf(2), leaf(3)))

  "leftMostLeaf creates a Lens from a Tree to its left most leaf" in {
    
    (leaf(1) |-> leftMostNode get) shouldEqual 1

    (tree    |-> leftMostNode get) shouldEqual 2

    (tree    |-> leftMostNode set 0) must equal (node(1, Stream(leaf(0), leaf(3))))

  }

  "leftMostLeaf creates a Lens from a Tree to its right most leaf" in {

    (leaf(1) |-> rightMostNode get) shouldEqual 1

    (tree    |-> rightMostNode get) shouldEqual 3

    (tree    |-> rightMostNode set 0) must equal (node(1, Stream(leaf(2), leaf(0))))

  }



}
