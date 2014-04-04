package monocle

import monocle.std.map._
import org.specs2.scalaz.Spec

class MapExample extends Spec {

  "at creates a Lens from a Map to an optional value" in {

    val map = Map("One" -> 1, "Two" -> 2)

    val atOne = at[String, Int]("One")

    atOne.get(map)            shouldEqual Some(1)
    atOne.set(map, Some(-1) ) shouldEqual Map("One" -> -1, "Two" -> 2)

    // with some syntax sugar
    import monocle.syntax.lens._

    (map |-> at("Two") get) shouldEqual Some(2)

    // can delete a value
    (map |-> at("Two") set None) shouldEqual Map("One" -> 1)

    // add a new value
    (map |-> at("Three") set Some(3)) shouldEqual Map("One" -> 1, "Two" -> 2, "Three" -> 3)

    // can be compose with some to simplify modification
    import monocle.std.option.some
    (map |-> at("One") |->> some modify(_ + 1)) shouldEqual Map("One" -> 2, "Two" -> 2)

  }

}
