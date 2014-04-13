package monocle

import org.specs2.scalaz.Spec
import monocle.function.Tail._
import monocle.syntax.traversal._


class TailExample extends Spec {

  "tail creates a Traversal from a List to its tail" in {
    (List(1, 2, 3)    |->> tail headOption) shouldEqual Some(List(2, 3))
    (List(1)          |->> tail headOption) shouldEqual Some(Nil)
    ((Nil: List[Int]) |->> tail headOption) shouldEqual None

    (List(1, 2, 3)    |->> tail set List(4, 5, 6)) shouldEqual List(1, 4, 5, 6)
  }

  "tail creates a Traversal from a Stream to its tail" in {
    (Stream(1, 2, 3)  |->> tail headOption) shouldEqual Some(Stream(2, 3))

    (Stream(1, 2, 3)    |->> tail set Stream.Empty) shouldEqual Stream(1)
  }

  "tail creates a Traversal from a String to its tail" in {
    ("hello" |->> tail modify (_.toUpperCase)) shouldEqual "hELLO"
  }

}
