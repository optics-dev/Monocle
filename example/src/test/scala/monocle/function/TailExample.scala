package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.OneAnd

class TailExample extends Spec {

  "tail creates a Lens from a 2-6 tuple to its tail" in {
    ((2, false) applyLens tail get)                      ==== false
    (('r', false, "lala", 5.6, 7, 4) applyLens tail get) ==== (false, "lala", 5.6, 7, 4)

    ((2, false, "hello") applyLens tail set (true, "plop")) ==== (2, true, "plop")
  }

  "tail creates a Lens from a OneAnd its first element" in {
    (OneAnd(1, List(2, 3)) applyLens tail get)  ==== List(2, 3)
  }

}