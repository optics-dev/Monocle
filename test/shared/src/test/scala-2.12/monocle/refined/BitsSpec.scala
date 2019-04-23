package monocle.refined

import eu.timepit.refined.W
import eu.timepit.refined.scalacheck.numeric._
import monocle.MonocleSuite
import monocle.law.discipline.function.AtTests

class BitsSpec extends MonocleSuite {
  checkAll("Byte at bit", AtTests[Byte, ZeroTo[W.`7`.T], Boolean])
  checkAll("Char at bit", AtTests[Char, ZeroTo[W.`15`.T], Boolean])
  checkAll("Int at bit", AtTests[Int, ZeroTo[W.`31`.T], Boolean])
  checkAll("Long at bit", AtTests[Long, ZeroTo[W.`63`.T], Boolean])
}

