package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function.{AtTests, EmptyTests}

class SetSpec extends MonocleSuite {
  checkAll("at Set", AtTests.defaultIntIndex[Set[Int], Option[Unit]])
  checkAll("empty Set", EmptyTests[Set[Int]])
}
