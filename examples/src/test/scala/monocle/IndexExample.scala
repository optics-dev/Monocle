package monocle

import monocle.syntax.traversal._
import monocle.util.Index._
import org.specs2.scalaz.Spec


class IndexExample extends Spec {

  "index creates 0 or 1 Traversal from a Map to a value" in {

    (Map("One" -> 1, "Two" -> 2) |->> index("One") headOption) shouldEqual Some(1)

    (Map("One" -> 1, "Two" -> 2) |->> index("One") set 2) shouldEqual Map("One" -> 2, "Two" -> 2)

  }

  "index creates 0 or 1 Traversal from a List to a value" in {

    (List(0,1,2,3) |->> index(1) headOption) shouldEqual Some(1)
    (List(0,1,2,3) |->> index(8) headOption) shouldEqual None

    (List(0,1,2,3) |->> index(1) modify(_ + 1)) shouldEqual List(0,2,2,3)
    // setting or modifying a value at an index without value is a no op
    (List(0,1,2,3) |->> index(64) set 10)       shouldEqual List(0,1,2,3)

  }

  "index creates 0 or 1 Traversal from a String to a Char" in {

    ("Hello World" |->> index(2) headOption) shouldEqual Some('l')

  }

}
