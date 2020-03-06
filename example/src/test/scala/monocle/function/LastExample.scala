package monocle.function

import monocle.MonocleSuite

class LastExample extends MonocleSuite {
  test("last creates a Lens from a 2-6 tuple to its last element") {
    ((2, false) applyLens last get) shouldEqual false
    (('r', false, "lala", 5.6, 7, 4) applyLens last get) shouldEqual 4

    ((2, false) applyLens last set true) shouldEqual ((2, true))
  }
}
