package monocle.function

import monocle.MonocleSuite

import scala.annotation.nowarn

@nowarn
class TailOptionExample extends MonocleSuite {
  test("tailOption creates an Optional from a List, Vector or Stream to its tail") {
    assertEquals((List(1, 2, 3) applyOptional tailOption getOption), Some(List(2, 3)))
    assertEquals((List(1) applyOptional tailOption getOption), Some(Nil))
    assertEquals(((Nil: List[Int]) applyOptional tailOption getOption), None)

    assertEquals((List(1, 2, 3) applyOptional tailOption replace List(4, 5, 6)), List(1, 4, 5, 6))
    assertEquals((Vector(1, 2, 3) applyOptional tailOption replace Vector(4, 5, 6)), Vector(1, 4, 5, 6))
  }

  test("tailOption creates an Optional from a String to its tail") {
    assertEquals(("hello" applyOptional tailOption modify (_.toUpperCase)), "hELLO")
  }
}
