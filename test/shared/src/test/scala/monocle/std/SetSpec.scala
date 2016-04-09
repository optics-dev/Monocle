package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function.{AtTests, EmptyTests}

class SetSpec extends MonocleSuite {
  checkAll("at Set", AtTests[Set[Int], Int, Boolean])
  checkAll("empty Set", EmptyTests[Set[Int]])
}
