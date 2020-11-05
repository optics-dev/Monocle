package monocle.function

import monocle.MonocleSuite

class FieldsExample extends MonocleSuite {
  test("_1 creates a Lens from a 2-6 tuple to its first element") {
    assertEquals((("Hello", 3) applyLens first get), "Hello")

    assertEquals((("Hello", 3, true) applyLens first set "World"), (("World", 3, true)))

    assertEquals((("Hello", 3, true, 5.6, 7L, 'c') applyLens first get), "Hello")
  }

  test("_2 creates a Lens from a 2-6 tuple to its second element") {
    assertEquals((("Hello", 3) applyLens second get), 3)

    assertEquals((("Hello", 3, true, 5.6, 7L, 'c') applyLens second set 4), (("Hello", 4, true, 5.6, 7L, 'c')))
  }

  // ...

  test("_6 creates a Lens from a 6-tuple to its sixth element") {
    assertEquals((("Hello", 3, true, 5.6, 7L, 'c') applyLens sixth get), 'c')

    assertEquals((("Hello", 3, true, 5.6, 7L, 'c') applyLens sixth set 'a'), (("Hello", 3, true, 5.6, 7L, 'a')))
  }
}
