package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{PrismLaws, IsoLaws, OptionalLaws, TraversalLaws}
import org.specs2.scalaz.Spec

class ListSpec extends Spec {

  checkAll("sequence List", SequenceLaws[List[Char], Char])

}
