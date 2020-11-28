package monocle.function

import monocle.MonocleSuite
import monocle.refined._
import shapeless.test.illTyped
import eu.timepit.refined.auto._

import scala.collection.immutable.SortedMap

class AtExample extends MonocleSuite {
  test("at creates a Lens from a Map, SortedMap to an optional value") {
    assertEquals((Map("One" -> 2, "Two" -> 2) applyLens at("Two") get), Some(2))
    assertEquals((SortedMap("One" -> 2, "Two" -> 2) applyLens at("Two") get), Some(2))

    assertEquals((Map("One" -> 1, "Two" -> 2) applyLens at("One") replace Some(-1)), Map("One" -> -1, "Two" -> 2))

    // can delete a value
    assertEquals((Map("One" -> 1, "Two" -> 2) applyLens at("Two") replace None), Map("One" -> 1))

    // add a new value
    assertEquals(
      (Map("One" -> 1, "Two" -> 2) applyLens at("Three") replace Some(3)),
      Map(
        "One"   -> 1,
        "Two"   -> 2,
        "Three" -> 3
      )
    )
  }

  test("at creates a Lens from a Set to an optional element of the Set") {
    assertEquals((Set(1, 2, 3) applyLens at(2) get), true)
    assertEquals((Set(1, 2, 3) applyLens at(4) get), false)

    assertEquals((Set(1, 2, 3) applyLens at(4) replace true), Set(1, 2, 3, 4))
    assertEquals((Set(1, 2, 3) applyLens at(2) replace false), Set(1, 3))
  }

  test("at creates a Lens from Int to one of its bit") {
    assertEquals((3 applyLens at(0: IntBits) get), true)  // true  means bit is 1
    assertEquals((4 applyLens at(0: IntBits) get), false) // false means bit is 0

    assertEquals((32 applyLens at(0: IntBits) replace true), 33)
    assertEquals((3 applyLens at(1: IntBits) modify (!_)), 1) // toggle 2nd bit

    illTyped("""0 applyLens at(79: IntBits) get""", "Right predicate.*fail.*")
    illTyped("""0 applyLens at(-1: IntBits) get""", "Left predicate.*fail.*")
  }

  test("at creates a Lens from Char to one of its bit") {
    assertEquals(('x' applyLens at(0: CharBits) get), false)
    assertEquals(('x' applyLens at(0: CharBits) replace true), 'y')
  }

  test("remove deletes an element of a Map") {
    assertEquals(remove("Foo")(Map("Foo" -> 1, "Bar" -> 2)), Map("Bar" -> 2))
  }
}
