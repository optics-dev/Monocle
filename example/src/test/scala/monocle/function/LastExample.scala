package monocle.function

import monocle.MonocleSuite

import scala.annotation.nowarn

@nowarn
class LastExample extends MonocleSuite {
  test("last creates a Lens from a 2-6 tuple to its last element") {
    assertEquals((2, false) applyLens last get, false)
    assertEquals(('r', false, "lala", 5.6, 7, 4) applyLens last get, 4)

    assertEquals((2, false) applyLens last replace true, (2, true))
  }
}
