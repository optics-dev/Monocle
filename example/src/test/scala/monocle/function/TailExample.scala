package monocle.function

import monocle.MonocleSuite

import cats.data.OneAnd

class TailExample extends MonocleSuite {
  test("tail creates a Lens from a 2-6 tuple to its tail") {
    ((2, false) applyLens tail get) shouldEqual false
    (('r', false, "lala", 5.6, 7, 4) applyLens tail get) shouldEqual ((false, "lala", 5.6, 7, 4))

    ((2, false, "hello") applyLens tail set ((true, "plop"))) shouldEqual ((2, true, "plop"))
  }

  test("tail creates a Lens from a OneAnd its first element") {
    (OneAnd(1, List(2, 3)) applyLens tail get) shouldEqual List(2, 3)
  }
}
