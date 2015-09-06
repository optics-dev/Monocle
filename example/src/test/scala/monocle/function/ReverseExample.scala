package monocle.function

import monocle._, Monocle._

import scalaz.Tree._

class ReverseExample extends MonocleSuite {

  test("reverse creates an Iso from a List to its reversed version") {
    (List(1,2,3) applyIso reverse get) shouldEqual List(3,2,1)
  }

  test("reverse creates an Iso from a tuple to its reversed version") {
    ((1,'b')                        applyIso reverse get) shouldEqual (('b',1))
    ((1,'b', true)                  applyIso reverse get) shouldEqual ((true, 'b',1))
    ((1,'b', true, 5.4, "plop", 7L) applyIso reverse get) shouldEqual ((7L, "plop", 5.4, true, 'b',1))

    // for tuple greater than 6 we need to use shapeless
    import monocle.generic.tuplen._
    ((1,'b', true, 5.4, "plop", 7L, false) applyIso reverse get) shouldEqual ((false, 7L, "plop", 5.4, true, 'b',1))
  }

  test("reverse creates an Iso from a Stream to its reversed version") {
    (Stream(1,2,3) applyIso reverse get) shouldEqual Stream(3,2,1)
  }

  test("reverse creates an Iso from a String to its reversed version") {
    ("Hello" applyIso reverse get) shouldEqual "olleH"
  }

  test("reverse creates an Iso from a Tree to its reversed version") {
    (node(1, Stream(leaf(2), leaf(3))) applyIso reverse get) shouldEqual node(1, Stream(leaf(3), leaf(2)))
  }

  test("reverse creates an Iso from a Vector to its reversed version") {
    (Vector(1,2,3) applyIso reverse get) shouldEqual Vector(3,2,1)
  }

}
