package monocle.function

import monocle.MonocleSuite

import scala.annotation.nowarn

@nowarn
class InitOptionExample extends MonocleSuite {
  test("tail creates a Traversal from a List, IList, Vector or Stream to its tail") {
    assertEquals((List(1, 2, 3) applyOptional initOption getOption), Some(List(1, 2)))
    assertEquals((List(1) applyOptional initOption getOption), Some(Nil))
    assertEquals(((Nil: List[Int]) applyOptional initOption getOption), None)

    assertEquals((List(1, 2, 3) applyOptional initOption replace List(4, 5, 6)), List(4, 5, 6, 3))
    assertEquals((Vector(1, 2, 3) applyOptional initOption replace Vector(4, 5, 6)), Vector(4, 5, 6, 3))
  }

  test("tail creates a Traversal from a String to its tail") {
    assertEquals(("hello" applyOptional initOption modify (_.toUpperCase)), "HELLo")
  }
}
