package monocle.function

import monocle.MonocleSuite

class HeadOptionExample extends MonocleSuite {
  test("headOption creates a Traversal from a List, Stream or Vector to its optional first element") {
    assertEquals((List(1, 2, 3) applyOptional headOption getOption), Some(1))
    assertEquals((Vector(1, 2, 3) applyOptional headOption getOption), Some(1))

    assertEquals((List.empty[Int] applyOptional headOption getOption), None)
    assertEquals((List.empty[Int] applyOptional headOption modify (_ + 1)), Nil)

    assertEquals((List(1, 2, 3) applyOptional headOption set 0), List(0, 2, 3))
    assertEquals((List(1, 2, 3) applyOptional headOption setOption 0), Some(List(0, 2, 3)))

    assertEquals((List.empty[Int] applyOptional headOption set 0), Nil)
    assertEquals((List.empty[Int] applyOptional headOption setOption 0), None)
  }

  test("headOption creates a Traversal from a String to its optional head Char") {
    assertEquals(("Hello" applyOptional headOption getOption), Some('H'))

    assertEquals(("Hello" applyOptional headOption set 'M'), "Mello")
  }
}
