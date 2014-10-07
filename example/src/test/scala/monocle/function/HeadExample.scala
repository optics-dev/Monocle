package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.OneAnd


class HeadExample extends Spec {

  "head creates a Lens from a 2-6 tuple to its first element" in {
    ((2, false) applyLens head get)                      ==== 2
    (('r', false, "lala", 5.6, 7, 4) applyLens head get) ==== 'r'

    ((2, false) applyLens head set 4) ==== ((4, false))
  }

  "head creates a Lens from a OneAnd its first element" in {
    (OneAnd(1, List(2, 3)) applyLens head get) ==== 1
  }

}
