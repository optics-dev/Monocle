package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec
import scalaz.Maybe

class AtExample extends Spec {

  "at creates a Lens from a Map to an optional value" in {
    (Map("One" -> 1, "Two" -> 2) applyLens at("Two") get) shouldEqual Maybe.just(2)

    (Map("One" -> 1, "Two" -> 2) applyLens at("One") set Maybe.just(-1))  shouldEqual Map("One" -> -1, "Two" -> 2)

    // can delete a value
    (Map("One" -> 1, "Two" -> 2) applyLens at("Two") set Maybe.empty) shouldEqual Map("One" -> 1)

    // add a new value
    (Map("One" -> 1, "Two" -> 2) applyLens at("Three") set Maybe.just(3)) shouldEqual Map("One" -> 1, "Two" -> 2, "Three" -> 3)
  }

  "at creates a Lens from a Set to an optional element of the Set" in {
    (Set(1, 2, 3) applyLens at(2) get) shouldEqual Maybe.just(())
    (Set(1, 2, 3) applyLens at(4) get) shouldEqual Maybe.empty

    (Set(1, 2, 3) applyLens at(4) set Maybe.just(()))  shouldEqual Set(1, 2, 3, 4)
    (Set(1, 2, 3) applyLens at(2) set Maybe.empty)     shouldEqual Set(1, 3)
  }

}
