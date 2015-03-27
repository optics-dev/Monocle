package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.IMap
import scalaz.std.string._

class AtExample extends Spec {

  "at creates a Lens from a Map, IMap to an optional value" in {
    (Map("One" -> 2, "Two" -> 2) applyLens at("Two") get) ==== Some(2)

    (Map("One" -> 1, "Two" -> 2) applyLens at("One") set Some(-1))  ==== Map("One" -> -1, "Two" -> 2)

    (IMap("One" -> 2, "Two" -> 2) applyLens at("Two") get) ==== Some(2)

    (IMap("One" -> 1, "Two" -> 2) applyLens at("One") set Some(-1))  ==== IMap("One" -> -1, "Two" -> 2)


    // can delete a value
    (Map("One" -> 1, "Two" -> 2) applyLens at("Two") set None) ==== Map("One" -> 1)
    (IMap("One" -> 1, "Two" -> 2) applyLens at("Two") set None) ==== IMap("One" -> 1)

    // add a new value
    (Map("One" -> 1, "Two" -> 2) applyLens at("Three") set Some(3)) ==== Map("One" -> 1, "Two" -> 2, "Three" -> 3)
    (IMap("One" -> 1, "Two" -> 2) applyLens at("Three") set Some(3)) ==== IMap("One" -> 1, "Two" -> 2, "Three" -> 3)
  }

  "at creates a Lens from a Set to an optional element of the Set" in {
    (Set(1, 2, 3) applyLens at(2) get) ==== Some(())
    (Set(1, 2, 3) applyLens at(4) get) ==== None

    (Set(1, 2, 3) applyLens at(4) set Some(())) ==== Set(1, 2, 3, 4)
    (Set(1, 2, 3) applyLens at(2) set None)     ==== Set(1, 3)
  }

}
