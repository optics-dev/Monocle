package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{PrismLaws, IsoLaws, OptionalLaws, TraversalLaws}
import org.specs2.scalaz.Spec

class StreamSpec extends Spec {

  checkAll("sequence Vector", SequenceLaws[Stream[Char], Char])

}
