package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

class InitExample extends Spec {

  "init creates a Lens from a 2-6 tuple to its tail" in {
    ((2, false) |-> init get)                      shouldEqual 2
    (('r', false, "lala", 5.6, 7, 4) |-> init get) shouldEqual ('r', false, "lala", 5.6, 7)

    ((2, false, "hello") |-> init set (4, true)) shouldEqual (4, true, "hello")
  }

}
