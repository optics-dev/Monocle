package monocle

import org.specs2.scalaz.Spec
import monocle.function.FilterIndex._
import monocle.syntax._
import scalaz.IList


class FilterIndexExample extends Spec {

  "filterIndexes creates Traversal from a Map to all values where the index matches the predicate" in {

    (Map("One" -> 1, "Two" -> 2) |->> filterIndex{k: String => k.toLowerCase.contains("o")} getAll) shouldEqual List(1, 2)

    (Map("One" -> 1, "Two" -> 2) |->> filterIndex{k: String => k.startsWith("T")} set 3) shouldEqual Map("One" -> 1, "Two" -> 3)

  }

  "filterIndexes creates Traversal from a List, IList, Vector or Stream to all values where the index matches the predicate" in {

    (List(1,3,5,7) |->> filterIndex{ i: Int => i%2 == 0 } getAll) shouldEqual List(1,5)

    (List(1,3,5,7)   |->> filterIndex{ i: Int => i >= 2 } modify(_ + 2)) shouldEqual List(1,3,7,9)
    (IList(1,3,5,7)  |->> filterIndex{ i: Int => i >= 2 } modify(_ + 2)) shouldEqual IList(1,3,7,9)
    (Vector(1,3,5,7) |->> filterIndex{ i: Int => i >= 2 } modify(_ + 2)) shouldEqual Vector(1,3,7,9)
    (Stream(1,3,5,7) |->> filterIndex{ i: Int => i >= 2 } modify(_ + 2)) shouldEqual Stream(1,3,7,9)

  }

}
