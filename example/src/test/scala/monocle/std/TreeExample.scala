package monocle.std

import monocle.MonocleSuite

import scalaz.Tree._
import scalaz.Tree

class TreeExample extends MonocleSuite {

  //     1
  //    / \
  //   2   3
  val tree = Node(1, Stream(Leaf(2), Leaf(3)))

  test("label creates a Lens from a Tree to its root label") {
    (tree applyLens rootLabel get) shouldEqual 1

    (tree applyLens rootLabel modify (_ - 1)) shouldEqual Node(0, Stream(Leaf(2), Leaf(3)))
  }

  test("subForest creates a Lens from a Tree to its children") {
    (Leaf(1) applyLens subForest get) shouldEqual Stream.Empty
    (tree    applyLens subForest get) shouldEqual Stream(Leaf(2), Leaf(3))

    (tree applyLens rootLabel modify (_ - 1)) shouldEqual (Node(0, Stream(Leaf(2), Leaf(3))))
  }

  test("leftMostLeaf creates a Lens from a Tree to its left most Leaf") {
    (Leaf(1) applyLens leftMostLabel get) shouldEqual 1
    (tree    applyLens leftMostLabel get) shouldEqual 2

    (tree    applyLens leftMostLabel set 0) shouldEqual Node(1, Stream(Leaf(0), Leaf(3)))
  }

  test("leftMostLeaf creates a Lens from a Tree to its right most Leaf") {
    (Leaf(1) applyLens rightMostLabel get) shouldEqual 1
    (tree    applyLens rightMostLabel get) shouldEqual 3

    (tree    applyLens rightMostLabel set 0) shouldEqual Node(1, Stream(Leaf(2), Leaf(0)))
  }

  test("Plated universe gives us a stream of all Node") {
    universe(tree) shouldEqual Stream(tree, Leaf(2), Leaf(3))
  }

  test("transformC transform Tree nodes counting number of changed nodes") {
    transformC[Tree[Int]] {
      case l@Leaf(3) => l.map(_ + 1)
    }(tree) shouldEqual ((1, Node(1, Stream(Leaf(2), Leaf(4)))))
  }

}
