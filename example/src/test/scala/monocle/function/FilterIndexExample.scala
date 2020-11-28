package monocle.function

import monocle.MonocleSuite

import scala.collection.immutable.SortedMap

class FilterIndexExample extends MonocleSuite {
  test("filterIndexes creates Traversal from a SortedMap, IMap to all values where the index matches the predicate") {
    assertEquals(
      (SortedMap("One" -> 1, "Two" -> 2) applyTraversal filterIndex { k: String =>
        k.toLowerCase.contains("o")
      } getAll),
      List(
        1,
        2
      )
    )
    assertEquals(
      (SortedMap("One" -> 1, "Two" -> 2) applyTraversal filterIndex { k: String =>
        k.startsWith("T")
      } replace 3),
      SortedMap(
        "One" -> 1,
        "Two" -> 3
      )
    )
  }

  test(
    "filterIndexes creates Traversal from a List, IList, Vector or Stream to all values where the index matches the predicate"
  ) {
    assertEquals((List(1, 3, 5, 7) applyTraversal filterIndex { i: Int => i % 2 == 0 } getAll), List(1, 5))

    assertEquals((List(1, 3, 5, 7) applyTraversal filterIndex { i: Int => i >= 2 } modify (_ + 2)), List(1, 3, 7, 9))
    assertEquals(
      (Vector(1, 3, 5, 7) applyTraversal filterIndex { i: Int => i >= 2 } modify (_ + 2)),
      Vector(1, 3, 7, 9)
    )
  }
}
