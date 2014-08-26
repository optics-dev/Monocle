package monocle.std

import monocle.TestUtil._
import monocle.syntax._
import org.specs2.scalaz.{ScalazMatchers, Spec}

import scalaz.Tree._

class TreeExample extends Spec with ScalazMatchers {

  //     1
  //    / \
  //   2   3
  val tree = node(1, Stream(leaf(2), leaf(3)))

  "label creates a Lens from a Tree to its root label" in {
    (tree applyLens rootLabel get) shouldEqual 1

    (tree applyLens rootLabel modify (_ - 1)) must equal (node(0, Stream(leaf(2), leaf(3))))
  }

  "subForest creates a Lens from a Tree to its children" in {
    (leaf(1) applyLens subForest get) shouldEqual Stream.Empty

    (tree applyLens subForest get) must equal (Stream(leaf(2), leaf(3)))

    (tree applyLens rootLabel modify (_ - 1)) must equal (node(0, Stream(leaf(2), leaf(3))))
  }

  "leftMostLeaf creates a Lens from a Tree to its left most leaf" in {
    (leaf(1) applyLens leftMostLabel get) shouldEqual 1

    (tree    applyLens leftMostLabel get) shouldEqual 2

    (tree    applyLens leftMostLabel set 0) must equal (node(1, Stream(leaf(0), leaf(3))))
  }

  "leftMostLeaf creates a Lens from a Tree to its right most leaf" in {
    (leaf(1) applyLens rightMostLabel get) shouldEqual 1

    (tree    applyLens rightMostLabel get) shouldEqual 3

    (tree    applyLens rightMostLabel set 0) must equal (node(1, Stream(leaf(2), leaf(0))))
  }



}
