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

  "at creates a Lens from a Set to an optional element of the Set" in {
    (Set(1, 2, 3) |-> at(2) get) shouldEqual Some(())
    (Set(1, 2, 3) |-> at(4) get) shouldEqual None

    (Set(1, 2, 3) |-> at(4) set Some(())) shouldEqual Set(1, 2, 3, 4)
    (Set(1, 2, 3) |-> at(2) set None)     shouldEqual Set(1, 3)
  }

}
