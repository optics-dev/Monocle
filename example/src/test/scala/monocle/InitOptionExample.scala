package monocle

import monocle.function.InitOption._
import monocle.std._
import monocle.scalazi.ilist._
import monocle.syntax._
import org.specs2.scalaz.Spec
import scalaz.IList


class InitOptionExample extends Spec {

  "tail creates a Traversal from a List, IList, Vector or Stream to its tail" in {
    (List(1, 2, 3)    |-? initOption getOption) shouldEqual Some(List(1, 2))
    (List(1)          |-? initOption getOption) shouldEqual Some(Nil)
    ((Nil: List[Int]) |-? initOption getOption) shouldEqual None

    (List(1, 2, 3)    |-? initOption set List(4, 5, 6))   shouldEqual List(4, 5, 6, 3)
    (IList(1, 2, 3)   |-? initOption set IList(4, 5, 6))  shouldEqual IList(4, 5, 6, 3)
    (Vector(1, 2, 3)  |-? initOption set Vector(4, 5, 6)) shouldEqual Vector(4, 5, 6, 3)
    (Stream(1, 2, 3)  |-? initOption set Stream(4, 5, 6)) shouldEqual Stream(4, 5, 6, 3)
  }

  "tail creates a Traversal from a String to its tail" in {
    ("hello" |-? initOption modify (_.toUpperCase)) shouldEqual "HELLo"
  }

}
