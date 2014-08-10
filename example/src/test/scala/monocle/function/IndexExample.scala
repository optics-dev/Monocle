package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.OneAnd


class IndexExample extends Spec {

  "index creates 0 or 1 Traversal from a Map to a value at the index" in {
    (Map("One" -> 1, "Two" -> 2) |-? index("One") getOption) shouldEqual Some(1)

    (Map("One" -> 1, "Two" -> 2) |-? index("One") set 2) shouldEqual Map("One" -> 2, "Two" -> 2)
  }

  "index creates 0 or 1 Traversal from a List, IList, Vector or Stream to a value at the index" in {
    (List(0,1,2,3) |-? index(1) getOption) shouldEqual Some(1)
    (List(0,1,2,3) |-? index(8) getOption) shouldEqual None

    (Vector(0,1,2,3) |-? index(1) modify(_ + 1)) shouldEqual Vector(0,2,2,3)
    // setting or modifying a value at an index without value is a no op
    (Stream(0,1,2,3) |-? index(64) set 10)       shouldEqual Stream(0,1,2,3)
  }

  "index creates 0 or 1 Traversal from a OneAnd to a value at the index" in {
    (OneAnd(1, List(2,3)) |-? index(0) getOption) shouldEqual Some(1)
    (OneAnd(1, List(2,3)) |-? index(1) getOption) shouldEqual Some(2)
  }

  "index creates 0 or 1 Traversal from a String to a Char" in {

    ("Hello World" |-? index(2) getOption) shouldEqual Some('l')

  }

}
