package monocle.function

import monocle.TestUtil._
import monocle._, Monocle._, generic.tuplen._
import org.specs2.scalaz.{ScalazMatchers, Spec}

import scalaz.Tree._

class ReverseExample extends Spec with ScalazMatchers {

  "reverse creates an Iso from a List to its reversed version" in {
    (List(1,2,3) applyIso reverse get) shouldEqual List(3,2,1)
  }

  "reverse creates an Iso from a tuple to its reversed version" in {
    ((1,'b')                        applyIso reverse get) shouldEqual ('b',1)
    ((1,'b', true)                  applyIso reverse get) shouldEqual (true, 'b',1)
    ((1,'b', true, 5.4, "plop", 7L) applyIso reverse get) shouldEqual (7L, "plop", 5.4, true, 'b',1)

    // for tuple greater than 6 we need to use shapeless
    ((1,'b', true, 5.4, "plop", 7L, false) applyIso reverse get) shouldEqual (false, 7L, "plop", 5.4, true, 'b',1)
  }

  "reverse creates an Iso from a Stream to its reversed version" in {
    (Stream(1,2,3) applyIso reverse get) shouldEqual Stream(3,2,1)
  }

  "reverse creates an Iso from a String to its reversed version" in {
    ("Hello" applyIso reverse get) shouldEqual "olleH"
  }

  "reverse creates an Iso from a Tree to its reversed version" in {
    (node(1, Stream(leaf(2), leaf(3))) applyIso reverse get) must equal (node(1, Stream(leaf(3), leaf(2))))
  }

  "reverse creates an Iso from a Vector to its reversed version" in {
    (Vector(1,2,3) applyIso reverse get) shouldEqual Vector(3,2,1)
  }

}
