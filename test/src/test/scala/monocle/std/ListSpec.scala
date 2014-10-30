package monocle.std

import monocle.TestUtil._
import monocle.law.function.SequenceLaws
import org.specs2.scalaz.Spec

class ListSpec extends Spec {

  checkAll("sequence List", SequenceLaws[List[Char], Char])

}
