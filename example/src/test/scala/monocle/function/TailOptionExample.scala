package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.{Maybe, IList}


class TailOptionExample extends Spec {

  "tailOption creates an Optional from a List, IList, Vector or Stream to its tail" in {
    (List(1, 2, 3)    applyOptional tailOption getMaybe) shouldEqual Maybe.just(List(2, 3))
    (List(1)          applyOptional tailOption getMaybe) shouldEqual Maybe.just(Nil)
    ((Nil: List[Int]) applyOptional tailOption getMaybe) shouldEqual Maybe.empty

    (List(1, 2, 3)    applyOptional tailOption set List(4, 5, 6))   shouldEqual List(1, 4, 5, 6)
    (IList(1, 2, 3)   applyOptional tailOption set IList(4, 5, 6))  shouldEqual IList(1, 4, 5, 6)
    (Vector(1, 2, 3)  applyOptional tailOption set Vector(4, 5, 6)) shouldEqual Vector(1, 4, 5, 6)
    (Stream(1, 2, 3)  applyOptional tailOption set Stream(4, 5, 6)) shouldEqual Stream(1, 4, 5, 6)
  }


  "tailOption creates an Optional from a String to its tail" in {
    ("hello" applyOptional tailOption modify (_.toUpperCase)) shouldEqual "hELLO"
  }

}
