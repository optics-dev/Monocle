package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{PrismLaws, IsoLaws, OptionalLaws, TraversalLaws}
import org.specs2.scalaz.Spec

import scalaz.IList


class IListSpec extends Spec {

  checkAll("sequence IList", SequenceLaws[IList[Char], Char])

}
