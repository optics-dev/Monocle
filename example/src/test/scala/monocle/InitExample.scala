package monocle

import monocle.function.Init._
import monocle.syntax.traversal._
import org.specs2.scalaz.Spec
import scalaz.IList


class InitExample extends Spec {

  "tail creates a Traversal from a List, IList, Vector or Stream to its tail" in {
    (List(1, 2, 3)    |->> init headOption) shouldEqual Some(List(1, 2))
    (List(1)          |->> init headOption) shouldEqual Some(Nil)
    ((Nil: List[Int]) |->> init headOption) shouldEqual None

    (List(1, 2, 3)    |->> init set List(4, 5, 6))   shouldEqual List(4, 5, 6, 3)
    (IList(1, 2, 3)   |->> init set IList(4, 5, 6))  shouldEqual IList(4, 5, 6, 3)
    (Vector(1, 2, 3)  |->> init set Vector(4, 5, 6)) shouldEqual Vector(4, 5, 6, 3)
    (Stream(1, 2, 3)  |->> init set Stream(4, 5, 6)) shouldEqual Stream(4, 5, 6, 3)
  }

  "tail creates a Traversal from a String to its tail" in {
    ("hello" |->> init modify (_.toUpperCase)) shouldEqual "HELLo"
  }

}
