package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.{IMap,IList}
import scalaz.std.string._


class FilterIndexExample extends Spec {

  "filterIndexes creates Traversal from a Map, IMap to all values where the index matches the predicate" in {

    (Map("One" -> 1, "Two" -> 2) applyTraversal filterIndex{k: String => k.toLowerCase.contains("o")} getAll) ==== IList(1, 2)
    (IMap("One" -> 1, "Two" -> 2) applyTraversal filterIndex{k: String => k.toLowerCase.contains("o")} getAll) ==== IList(1, 2)

    (Map("One" -> 1, "Two" -> 2) applyTraversal filterIndex{k: String => k.startsWith("T")} set 3) ==== Map("One" -> 1, "Two" -> 3)
    (IMap("One" -> 1, "Two" -> 2) applyTraversal filterIndex{k: String => k.startsWith("T")} set 3) ==== IMap("One" -> 1, "Two" -> 3)

  }

  "filterIndexes creates Traversal from a List, IList, Vector or Stream to all values where the index matches the predicate" in {

    (List(1,3,5,7) applyTraversal filterIndex{ i: Int => i%2 == 0 } getAll) ==== IList(1,5)

    (List(1,3,5,7)   applyTraversal filterIndex{ i: Int => i >= 2 } modify(_ + 2)) ==== List(1,3,7,9)
    (IList(1,3,5,7)  applyTraversal filterIndex{ i: Int => i >= 2 } modify(_ + 2)) ==== IList(1,3,7,9)
    (Vector(1,3,5,7) applyTraversal filterIndex{ i: Int => i >= 2 } modify(_ + 2)) ==== Vector(1,3,7,9)
    (Stream(1,3,5,7) applyTraversal filterIndex{ i: Int => i >= 2 } modify(_ + 2)) ==== Stream(1,3,7,9)

  }

}
