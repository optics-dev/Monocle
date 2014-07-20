package monocle

import _root_.scalaz.OneAnd
import monocle.function.Tail._
import monocle.std._
import monocle.scalaz.oneand._
import monocle.syntax._
import org.specs2.scalaz.Spec

class TailExample extends Spec {

  "tail creates a Lens from a 2-6 tuple to its tail" in {
    ((2, false) ^|-> tail get)                      shouldEqual false
    (('r', false, "lala", 5.6, 7, 4) ^|-> tail get) shouldEqual (false, "lala", 5.6, 7, 4)

    ((2, false, "hello") ^|-> tail set (true, "plop")) shouldEqual (2, true, "plop")
  }

  "head creates a Lens from a OneAnd its first element" in {
    (OneAnd(1, List(2, 3)) ^|-> tail get)  shouldEqual List(2, 3)
  }

}