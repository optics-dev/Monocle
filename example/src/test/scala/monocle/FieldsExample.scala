package monocle

import org.specs2.scalaz.Spec
import monocle.function.Fields._
import monocle.syntax._


class FieldsExample extends Spec {

  "_1 creates a Lens from a 2-6 tuple to its first element" in {
    (("Hello", 3) |-> _1 get) shouldEqual "Hello"

    (("Hello", 3, true) |-> _1 set "World") shouldEqual ("World", 3, true)

    (("Hello", 3, true, 5.6, 7L, 'c') |-> _1 get) shouldEqual "Hello"
  }

  "_2 creates a Lens from a 2-6 tuple to its second element" in {
    (("Hello", 3) |-> _2 get) shouldEqual 3

    (("Hello", 3, true, 5.6, 7L, 'c') |-> _2 set 4) shouldEqual ("Hello", 4, true, 5.6, 7L, 'c')
  }

  // ...

  "_6 creates a Lens from a 6-tuple to its sixth element" in {
    (("Hello", 3, true, 5.6, 7L, 'c') |-> _6 get) shouldEqual 'c'

    (("Hello", 3, true, 5.6, 7L, 'c') |-> _6 set 'a') shouldEqual ("Hello", 3, true, 5.6, 7L, 'a')
  }

}
