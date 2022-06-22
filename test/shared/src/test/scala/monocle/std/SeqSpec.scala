package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function._

class SeqSpec extends MonocleSuite {
  checkAll("index Seq", IndexTests[Seq[Int], Int, Int])
}
