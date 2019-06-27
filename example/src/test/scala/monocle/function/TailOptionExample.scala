package monocle.function

import monocle.MonocleSuite

class TailOptionExample extends MonocleSuite {

  test("tailOption creates an Optional from a List, Vector or Stream to its tail") {
    (List(1, 2, 3)    applyOptional tailOption getOption) shouldEqual Some(List(2, 3))
    (List(1)          applyOptional tailOption getOption) shouldEqual Some(Nil)
    ((Nil: List[Int]) applyOptional tailOption getOption) shouldEqual None

    (List(1, 2, 3)    applyOptional tailOption set List(4, 5, 6))   shouldEqual List(1, 4, 5, 6)
    (Vector(1, 2, 3)  applyOptional tailOption set Vector(4, 5, 6)) shouldEqual Vector(1, 4, 5, 6)
  }

  test("tailOption creates an Optional from a String to its tail") {
    ("hello" applyOptional tailOption modify (_.toUpperCase)) shouldEqual "hELLO"
  }

}
