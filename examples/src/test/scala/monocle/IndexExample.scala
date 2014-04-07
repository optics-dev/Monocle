package monocle

import monocle.syntax.traversal._
import monocle.util.Index._
import org.specs2.scalaz.Spec


class IndexExample extends Spec {

  "index creates 0 or 1 Traversal from a Map to a value" in {

    (Map("One" -> 1, "Two" -> 2) |->> index("One") headOption) shouldEqual Some(1)

    (Map("One" -> 1, "Two" -> 2) |->> index("One") set 2) shouldEqual Map("One" -> 2, "Two" -> 2)

  }

  "filterIndexes creates Traversal from a Map to all values where the index matches the predicate" in {

    (Map("One" -> 1, "Two" -> 2) |->> filterIndexes{k: String => k.toLowerCase.contains("o")} getAll) shouldEqual List(1, 2)

    (Map("One" -> 1, "Two" -> 2) |->> filterIndexes{k: String => k.startsWith("T")} set 3) shouldEqual Map("One" -> 1, "Two" -> 3)

  }

  "index creates 0 or 1 Traversal from a List to a value" in {

    (List(0,1,2,3) |->> index(1) headOption) shouldEqual Some(1)
    (List(0,1,2,3) |->> index(8) headOption) shouldEqual None

    (List(0,1,2,3) |->> index(1) modify(_ + 1)) shouldEqual List(0,2,2,3)
    // setting or modifying a value at an index without value is a no op
    (List(0,1,2,3) |->> index(64) set 10)       shouldEqual List(0,1,2,3)

  }

  "filterIndexes creates Traversal from a List to all values where the index matches the predicate" in {

    (List(1,3,5,7) |->> filterIndexes{ i: Int => i%2 == 0 } getAll) shouldEqual List(1,5)

    (List(1,3,5,7) |->> filterIndexes{ i: Int => i >= 2 } modify(_ + 2)) shouldEqual List(1,3,7,9)

  }

  "index creates 0 or 1 Traversal from a String to a Char" in {

    ("Hello World" |->> index(2) headOption) shouldEqual Some('l')

  }

}
