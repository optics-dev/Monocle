package monocle.function

import monocle.MonocleSuite

class FieldsExample extends MonocleSuite {
  test("_1 creates a Lens from a 2-6 tuple to its first element") {
    assertEquals(("Hello", 3).optics.at(1).get, "Hello")

    assertEquals(("Hello", 3, true).optics.at(1).replace("World"), (("World", 3, true)))

    assertEquals(("Hello", 3, true, 5.6, 7L, 'c').optics.at(1).get, "Hello")
  }

  test("_2 creates a Lens from a 2-6 tuple to its second element") {
    assertEquals(("Hello", 3).optics.at(2).get, 3)

    assertEquals(("Hello", 3, true, 5.6, 7L, 'c').optics.at(2).replace(4), (("Hello", 4, true, 5.6, 7L, 'c')))
  }

  // ...

  test("_6 creates a Lens from a 6-tuple to its sixth element") {
    assertEquals(("Hello", 3, true, 5.6, 7L, 'c').optics.at(6).get, 'c')

    assertEquals(("Hello", 3, true, 5.6, 7L, 'c').optics.at(6).replace('a'), (("Hello", 3, true, 5.6, 7L, 'a')))
  }
}
