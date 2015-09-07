package monocle.function

import monocle.MonocleSuite

import scalaz.std.string._
import scalaz.{IList, IMap}

class FilterIndexExample extends MonocleSuite {

  test("filterIndexes creates Traversal from a Map, IMap to all values where the index matches the predicate") {

    (Map("One" -> 1, "Two" -> 2) applyTraversal filterIndex{k: String => k.toLowerCase.contains("o")} getAll)  shouldEqual List(1, 2)
    (IMap("One" -> 1, "Two" -> 2) applyTraversal filterIndex{k: String => k.toLowerCase.contains("o")} getAll) shouldEqual List(1, 2)

    (Map("One" -> 1, "Two" -> 2) applyTraversal filterIndex{k: String => k.startsWith("T")} set 3) shouldEqual Map("One" -> 1, "Two" -> 3)
    (IMap("One" -> 1, "Two" -> 2) applyTraversal filterIndex{k: String => k.startsWith("T")} set 3) shouldEqual IMap("One" -> 1, "Two" -> 3)

  }

  test("filterIndexes creates Traversal from a List, IList, Vector or Stream to all values where the index matches the predicate") {

    (List(1,3,5,7) applyTraversal filterIndex{ i: Int => i%2 == 0 } getAll) shouldEqual List(1,5)

    (List(1,3,5,7)   applyTraversal filterIndex{ i: Int => i >= 2 } modify(_ + 2)) shouldEqual List(1,3,7,9)
    (IList(1,3,5,7)  applyTraversal filterIndex{ i: Int => i >= 2 } modify(_ + 2)) shouldEqual IList(1,3,7,9)
    (Vector(1,3,5,7) applyTraversal filterIndex{ i: Int => i >= 2 } modify(_ + 2)) shouldEqual Vector(1,3,7,9)
    (Stream(1,3,5,7) applyTraversal filterIndex{ i: Int => i >= 2 } modify(_ + 2)) shouldEqual Stream(1,3,7,9)

  }

}
