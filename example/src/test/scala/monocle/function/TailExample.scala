package monocle.function

import monocle.MonocleSuite

import cats.data.OneAnd

class TailExample extends MonocleSuite {
  test("tail creates a Lens from a 2-6 tuple to its tail") {
    assertEquals(((2, false) applyLens tail get), false)
    assertEquals((('r', false, "lala", 5.6, 7, 4) applyLens tail get), ((false, "lala", 5.6, 7, 4)))

    assertEquals(((2, false, "hello") applyLens tail replace ((true, "plop"))), ((2, true, "plop")))
  }

  test("tail creates a Lens from a OneAnd its first element") {
    assertEquals((OneAnd(1, List(2, 3)) applyLens tail get), List(2, 3))
  }
}
