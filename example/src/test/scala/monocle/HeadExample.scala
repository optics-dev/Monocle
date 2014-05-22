package monocle

import monocle.function.Head._
import monocle.syntax._
import org.specs2.scalaz.Spec


class HeadExample extends Spec {

  "head creates a Lens from a 2-6 tuple to its first element" in {
    ((2, false) |-> head get)                      shouldEqual 2
    (('r', false, "lala", 5.6, 7, 4) |-> head get) shouldEqual 'r'

    ((2, false) |-> head set 4) shouldEqual (4, false)

  }

}
