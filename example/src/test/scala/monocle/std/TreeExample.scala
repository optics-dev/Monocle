package monocle.std

import monocle.MonocleSuite
import monocle.syntax._

import scalaz.Tree._

class TreeExample extends MonocleSuite {

  //     1
  //    / \
  //   2   3
  val tree = node(1, Stream(leaf(2), leaf(3)))

  test("label creates a Lens from a Tree to its root label") {
    (tree applyLens rootLabel get) shouldEqual 1

    (tree applyLens rootLabel modify (_ - 1)) shouldEqual node(0, Stream(leaf(2), leaf(3)))
  }

  test("subForest creates a Lens from a Tree to its children") {
    (leaf(1) applyLens subForest get) shouldEqual Stream.Empty
    (tree    applyLens subForest get) shouldEqual Stream(leaf(2), leaf(3))

    (tree applyLens rootLabel modify (_ - 1)) shouldEqual (node(0, Stream(leaf(2), leaf(3))))
  }

  test("leftMostLeaf creates a Lens from a Tree to its left most leaf") {
    (leaf(1) applyLens leftMostLabel get) shouldEqual 1
    (tree    applyLens leftMostLabel get) shouldEqual 2

    (tree    applyLens leftMostLabel set 0) shouldEqual node(1, Stream(leaf(0), leaf(3)))
  }

  test("leftMostLeaf creates a Lens from a Tree to its right most leaf") {
    (leaf(1) applyLens rightMostLabel get) shouldEqual 1
    (tree    applyLens rightMostLabel get) shouldEqual 3

    (tree    applyLens rightMostLabel set 0) shouldEqual node(1, Stream(leaf(2), leaf(0)))
  }

}
