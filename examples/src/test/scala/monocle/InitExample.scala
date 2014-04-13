package monocle

import org.specs2.scalaz.Spec
import monocle.syntax.traversal._
import monocle.function.Init._


class InitExample extends Spec {

  "tail creates a Traversal from a List to its tail" in {
    (List(1, 2, 3)    |->> init headOption) shouldEqual Some(List(1, 2))
    (List(1)          |->> init headOption) shouldEqual Some(Nil)
    ((Nil: List[Int]) |->> init headOption) shouldEqual None

    (List(1, 2, 3)    |->> init set List(4, 5, 6)) shouldEqual List(4, 5, 6, 3)
  }

  "tail creates a Traversal from a Stream to its tail" in {
    (Stream(1, 2, 3) |->> init headOption) shouldEqual Some(Stream(1, 2))

    (Stream(1, 2, 3) |->> init set Stream.Empty) shouldEqual Stream(3)
  }

  "tail creates a Traversal from a String to its tail" in {
    ("hello" |->> init modify (_.toUpperCase)) shouldEqual "HELLo"
  }

}
