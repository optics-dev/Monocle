package monocle.std

import monocle.TestUtil._
import monocle.law.function.SequenceLaws
import org.specs2.scalaz.Spec

class StreamSpec extends Spec {

  checkAll("sequence Vector", SequenceLaws[Stream[Char], Char])

}
