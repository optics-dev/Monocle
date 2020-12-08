package monocle.function

import monocle.MonocleSuite

import scala.annotation.nowarn

@nowarn
class InitExample extends MonocleSuite {
  test("init creates a Lens from a 2-6 tuple to its tail") {
    assertEquals(((2, false) applyLens init get), 2)
    assertEquals((('r', false, "lala", 5.6, 7, 4) applyLens init get), (('r', false, "lala", 5.6, 7)))

    assertEquals(((2, false, "hello") applyLens init replace ((4, true))), ((4, true, "hello")))
  }
}
