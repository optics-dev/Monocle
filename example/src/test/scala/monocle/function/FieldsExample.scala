package monocle.function

import monocle.MonocleSuite
import monocle.std._
import monocle.syntax._


class FieldsExample extends MonocleSuite {

  test("_1 creates a Lens from a 2-6 tuple to its first element") {
    (("Hello", 3) applyLens first get) shouldEqual "Hello"

    (("Hello", 3, true) applyLens first set "World") shouldEqual (("World", 3, true))

    (("Hello", 3, true, 5.6, 7L, 'c') applyLens first get) shouldEqual "Hello"
  }

  test("_2 creates a Lens from a 2-6 tuple to its second element") {
    (("Hello", 3) applyLens second get) shouldEqual 3

    (("Hello", 3, true, 5.6, 7L, 'c') applyLens second set 4) shouldEqual (("Hello", 4, true, 5.6, 7L, 'c'))
  }

  // ...

  test("_6 creates a Lens from a 6-tuple to its sixth element") {
    (("Hello", 3, true, 5.6, 7L, 'c') applyLens sixth get) shouldEqual 'c'

    (("Hello", 3, true, 5.6, 7L, 'c') applyLens sixth set 'a') shouldEqual (("Hello", 3, true, 5.6, 7L, 'a'))
  }

}
