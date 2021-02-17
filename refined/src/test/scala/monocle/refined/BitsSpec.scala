package monocle.refined

import eu.timepit.refined.scalacheck.numeric._
import monocle.refined.all._
import monocle.law.discipline.function.AtTests

class BitsSpec extends munit.DisciplineSuite {
  checkAll("Byte at bit", AtTests[Byte, ZeroTo[7], Boolean])
  checkAll("Char at bit", AtTests[Char, ZeroTo[15], Boolean])
  checkAll("Int at bit", AtTests[Int, ZeroTo[31], Boolean])
  checkAll("Long at bit", AtTests[Long, ZeroTo[63], Boolean])
}
