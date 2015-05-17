package monocle.function

import monocle.MonocleSuite
import monocle.std._
import monocle.syntax._

import scalaz.OneAnd


class HeadExample extends MonocleSuite {

  test("head creates a Lens from a 2-6 tuple to its first element") {
    ((2, false) applyLens head get)                      shouldEqual 2
    (('r', false, "lala", 5.6, 7, 4) applyLens head get) shouldEqual 'r'

    ((2, false) applyLens head set 4) shouldEqual ((4, false))
  }

  test("head creates a Lens from a OneAnd its first element") {
    (OneAnd(1, List(2, 3)) applyLens head get) shouldEqual 1
  }

}
