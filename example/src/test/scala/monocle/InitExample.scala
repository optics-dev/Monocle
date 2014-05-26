package monocle

import org.specs2.scalaz.Spec
import monocle.function.Init._
import monocle.syntax.lens._

class InitExample extends Spec {

  "init creates a Lens from a 2-6 tuple to its tail" in {
    ((2, false) |-> init get)                      shouldEqual 2
    (('r', false, "lala", 5.6, 7, 4) |-> init get) shouldEqual ('r', false, "lala", 5.6, 7)

    ((2, false, "hello") |-> init set (4, true)) shouldEqual (4, true, "hello")
  }

}
