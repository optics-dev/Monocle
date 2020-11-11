package monocle.function

import monocle.MonocleSuite

class LastOptionExample extends MonocleSuite {
  test("lastOption creates a Traversal from a List, Stream or Vector to its optional last element") {
    assertEquals((List(1, 2, 3) applyOptional lastOption getOption), Some(3))
    assertEquals((Vector(1, 2, 3) applyOptional lastOption getOption), Some(3))

    assertEquals((List.empty[Int] applyOptional lastOption getOption), None)
    assertEquals((List.empty[Int] applyOptional lastOption modify (_ + 1)), Nil)

    assertEquals((List(1, 2, 3) applyOptional lastOption set 0), List(1, 2, 0))
  }

  test("lastOption creates a Traversal from a String to its optional last Char") {
    assertEquals(("Hello" applyOptional lastOption getOption), Some('o'))

    assertEquals(("Hello" applyOptional lastOption set 'a'), "Hella")
  }
}
