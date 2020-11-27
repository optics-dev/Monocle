package monocle.function

import monocle.MonocleSuite

class FieldsExample extends MonocleSuite {
  test("_1 creates a Lens from a 2-6 tuple to its first element") {
    assertEquals((("Hello", 3) applyLens at(1) get), "Hello")

    assertEquals((("Hello", 3, true) applyLens at(1) set "World"), (("World", 3, true)))

    assertEquals((("Hello", 3, true, 5.6, 7L, 'c') applyLens at(1) get), "Hello")
  }

  test("_2 creates a Lens from a 2-6 tuple to its second element") {
    assertEquals((("Hello", 3) applyLens at(2) get), 3)

    assertEquals((("Hello", 3, true, 5.6, 7L, 'c') applyLens at(2) set 4), (("Hello", 4, true, 5.6, 7L, 'c')))
  }

  // ...

  test("_6 creates a Lens from a 6-tuple to its sixth element") {
    assertEquals((("Hello", 3, true, 5.6, 7L, 'c') applyLens at(6) get), 'c')

    assertEquals((("Hello", 3, true, 5.6, 7L, 'c') applyLens at(6) set 'a'), (("Hello", 3, true, 5.6, 7L, 'a')))
  }
}
