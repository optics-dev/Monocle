package monocle

import scalaz.IList
import monocle.function.TailOption._
import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec


class TailOptionExample extends Spec {

  "tailOption creates an Optional from a List, IList, Vector or Stream to its tail" in {
    (List(1, 2, 3)    |-? tailOption getOption) shouldEqual Some(List(2, 3))
    (List(1)          |-? tailOption getOption) shouldEqual Some(Nil)
    ((Nil: List[Int]) |-? tailOption getOption) shouldEqual None

    (List(1, 2, 3)    |-? tailOption set List(4, 5, 6))   shouldEqual List(1, 4, 5, 6)
    (IList(1, 2, 3)   |-? tailOption set IList(4, 5, 6))  shouldEqual IList(1, 4, 5, 6)
    (Vector(1, 2, 3)  |-? tailOption set Vector(4, 5, 6)) shouldEqual Vector(1, 4, 5, 6)
    (Stream(1, 2, 3)  |-? tailOption set Stream(4, 5, 6)) shouldEqual Stream(1, 4, 5, 6)
  }


  "tailOption creates an Optional from a String to its tail" in {
    ("hello" |-? tailOption modify (_.toUpperCase)) shouldEqual "hELLO"
  }

}
