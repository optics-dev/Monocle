package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

class AtExample extends Spec {

  "at creates a Lens from a Map to an optional value" in {

    (Map("One" -> 1, "Two" -> 2) |-> at("Two") get) shouldEqual Some(2)

    (Map("One" -> 1, "Two" -> 2) |-> at("One") set Some(-1))  shouldEqual Map("One" -> -1, "Two" -> 2)

    // can delete a value
    (Map("One" -> 1, "Two" -> 2) |-> at("Two") set None) shouldEqual Map("One" -> 1)

    // add a new value
    (Map("One" -> 1, "Two" -> 2) |-> at("Three") set Some(3)) shouldEqual Map("One" -> 1, "Two" -> 2, "Three" -> 3)

  }

}
