package monocle.function

import monocle.MonocleSuite
import cats.data.OneAnd

import scala.annotation.nowarn

@nowarn
class HeadExample extends MonocleSuite {
  test("head creates a Lens from a 2-6 tuple to its first element") {
    assertEquals(((2, false) applyLens head get), 2)
    assertEquals((('r', false, "lala", 5.6, 7, 4) applyLens head get), 'r')

    assertEquals(((2, false) applyLens head replace 4), ((4, false)))
  }

  test("head creates a Lens from a OneAnd its first element") {
    assertEquals((OneAnd(1, List(2, 3)) applyLens head get), 1)
  }
}
