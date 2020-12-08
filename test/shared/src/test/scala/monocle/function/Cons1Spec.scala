package monocle.function

import monocle.MonocleSuite
import monocle.law.discipline.function.Cons1Tests

import scala.annotation.nowarn

@nowarn
class Cons1Spec extends MonocleSuite {
  implicit val clistCons1: Cons1[CNel, Char, List[Char]] = Cons1.fromIso(CNel.toNel)

  checkAll("fromIso", Cons1Tests[CNel, Char, List[Char]])
}
