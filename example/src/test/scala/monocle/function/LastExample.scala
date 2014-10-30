package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.OneAnd


class LastExample extends Spec {

  "last creates a Lens from a 2-6 tuple to its last element" in {
    ((2, false) applyLens last get)                      ==== false
    (('r', false, "lala", 5.6, 7, 4) applyLens last get) ==== 4

    ((2, false) applyLens last set true) ==== ((2, true))
  }

}
