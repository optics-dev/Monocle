package monocle.std

import monocle.TestUtil._
import monocle.function._
import org.specs2.scalaz.Spec

class VectorSpec extends Spec {

  checkAll("sequence Vector", SequenceLaws[Vector[Char], Char])

}
