package monocle

import monocle.function.Tail._
import monocle.syntax.traversal._
import org.specs2.scalaz.Spec
import scalaz.IList


class TailExample extends Spec {

  "tail creates a Traversal from a List, IList, Vector or Stream to its tail" in {
    (List(1, 2, 3)    |->> tail headOption) shouldEqual Some(List(2, 3))
    (List(1)          |->> tail headOption) shouldEqual Some(Nil)
    ((Nil: List[Int]) |->> tail headOption) shouldEqual None

    (List(1, 2, 3)    |->> tail set List(4, 5, 6))   shouldEqual List(1, 4, 5, 6)
    (IList(1, 2, 3)   |->> tail set IList(4, 5, 6))  shouldEqual IList(1, 4, 5, 6)
    (Vector(1, 2, 3)  |->> tail set Vector(4, 5, 6)) shouldEqual Vector(1, 4, 5, 6)
    (Stream(1, 2, 3)  |->> tail set Stream(4, 5, 6)) shouldEqual Stream(1, 4, 5, 6)
  }


  "tail creates a Traversal from a String to its tail" in {
    ("hello" |->> tail modify (_.toUpperCase)) shouldEqual "hELLO"
  }

}
