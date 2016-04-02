package monocle.function

import monocle.MonocleSuite
import monocle.law.discipline.function.Snoc1Tests

class Snoc1Spec extends MonocleSuite {

  implicit val clistSnoc1: Snoc1[CNel, List[Char], Char] = Snoc1.fromIso(CNel.toNel)

  checkAll("fromIso", Snoc1Tests[CNel, List[Char], Char])

}
