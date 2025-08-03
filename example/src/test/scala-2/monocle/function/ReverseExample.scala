package monocle.function

import monocle._
import monocle.generic.GenericInstances

import scala.annotation.nowarn

@nowarn
class ReverseExample extends MonocleSuite with GenericInstances {
  test("reverse creates an Iso from a List to its reversed version") {
    assertEquals(List(1, 2, 3) applyIso reverse get, List(3, 2, 1))
  }

  test("reverse creates an Iso from a tuple to its reversed version") {
    assertEquals((1, 'b') applyIso reverse get, ('b', 1))
    assertEquals((1, 'b', true) applyIso reverse get, (true, 'b', 1))
    assertEquals((1, 'b', true, 5.4, "plop", 7L) applyIso reverse get, (7L, "plop", 5.4, true, 'b', 1))

    // for tuple greater than 6 we need to use shapeless
    assertEquals(
      (1, 'b', true, 5.4, "plop", 7L, false) applyIso reverse get,
      (false, 7L, "plop", 5.4, true, 'b', 1)
    )
  }

  test("reverse creates an Iso from a String to its reversed version") {
    assertEquals("Hello" applyIso reverse get, "olleH")
  }

  test("reverse creates an Iso from a Vector to its reversed version") {
    assertEquals(Vector(1, 2, 3) applyIso reverse get, Vector(3, 2, 1))
  }
}
