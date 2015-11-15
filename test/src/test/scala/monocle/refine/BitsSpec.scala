package monocle.refine

import eu.timepit.refined._
import eu.timepit.refined.auto._
import monocle.MonocleSuite
import monocle.law.discipline.function.AtTests

class BitsSpec extends MonocleSuite {
  checkAll("Byte at bit", AtTests[Byte, ZeroTo[W.`7`.T], Boolean](0))
  checkAll("Char at bit", AtTests[Char, ZeroTo[W.`15`.T], Boolean](0))
  checkAll("Int at bit", AtTests[Int, ZeroTo[W.`31`.T], Boolean](0))
  checkAll("Long at bit", AtTests[Long, ZeroTo[W.`63`.T], Boolean](0))
}
