package monocle

import monocle.scalazi.tree
import tree._
import org.specs2.scalaz.{ScalazMatchers, Spec}
import scalaz.Tree._
import monocle.TestUtil._
import monocle.syntax._

class TreeExample extends Spec with ScalazMatchers {

  //     1
  //    / \
  //   2   3
  val tree = node(1, Stream(leaf(2), leaf(3)))

  "label creates a Lens from a Tree to its root label" in {
    (tree |-> rootLabel get) shouldEqual 1

    (tree |-> rootLabel modify (_ - 1)) must equal (node(0, Stream(leaf(2), leaf(3))))
  }

  "subForest creates a Lens from a Tree to its children" in {
    (leaf(1) |-> subForest get) shouldEqual Stream.Empty

    (tree |-> subForest get) must equal (Stream(leaf(2), leaf(3)))

    (tree |-> rootLabel modify (_ - 1)) must equal (node(0, Stream(leaf(2), leaf(3))))
  }

  "leftMostLeaf creates a Lens from a Tree to its left most leaf" in {
    (leaf(1) |-> leftMostLabel get) shouldEqual 1

    (tree    |-> leftMostLabel get) shouldEqual 2

    (tree    |-> leftMostLabel set 0) must equal (node(1, Stream(leaf(0), leaf(3))))
  }

  "leftMostLeaf creates a Lens from a Tree to its right most leaf" in {
    (leaf(1) |-> rightMostLabel get) shouldEqual 1

    (tree    |-> rightMostLabel get) shouldEqual 3

    (tree    |-> rightMostLabel set 0) must equal (node(1, Stream(leaf(2), leaf(0))))
  }



}
