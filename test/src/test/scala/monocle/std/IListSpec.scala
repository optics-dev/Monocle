package monocle.std

import monocle.TestUtil._
import monocle.law.function.SequenceLaws
import org.specs2.scalaz.Spec

import scalaz.IList


class IListSpec extends Spec {

  checkAll("sequence IList", SequenceLaws[IList[Char], Char])

}
