package monocle

import monocle.function.Last._
import monocle.syntax.lens._
import org.specs2.scalaz.Spec


class LastExample extends Spec {

  "last creates a Lens from a 2-6 tuple to its last element" in {
    ((2, false) |-> last get)                      shouldEqual false
    (('r', false, "lala", 5.6, 7, 4) |-> last get) shouldEqual 4

    ((2, false) |-> last set true) shouldEqual (2, true)
  }

}
