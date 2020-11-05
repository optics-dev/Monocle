package monocle.function

import monocle.MonocleSuite

import scala.collection.immutable.Map
import cats.data.OneAnd

class IndexExample extends MonocleSuite {
  test("index creates an Optional from a Map, IMap to a value at the index") {
    assertEquals((Map("One" -> 1, "Two" -> 2) applyOptional index("One") getOption), Some(1))

    assertEquals((Map("One" -> 1, "Two" -> 2) applyOptional index("One") set 2), Map("One" -> 2, "Two" -> 2))
  }

  test("index creates an Optional from a List, Vector or Stream to a value at the index") {
    assertEquals((List(0, 1, 2, 3) applyOptional index(1) getOption), Some(1))
    assertEquals((List(0, 1, 2, 3) applyOptional index(8) getOption), None)

    assertEquals((Vector(0, 1, 2, 3) applyOptional index(1) modify (_ + 1)), Vector(0, 2, 2, 3))
  }

  test("index creates an Optional from a OneAnd to a value at the index") {
    assertEquals((OneAnd(1, List(2, 3)) applyOptional index(0) getOption), Some(1))
    assertEquals((OneAnd(1, List(2, 3)) applyOptional index(1) getOption), Some(2))
  }

  test("index creates an Optional from a String to a Char") {
    assertEquals(("Hello World" applyOptional index(2) getOption), Some('l'))
  }
}
