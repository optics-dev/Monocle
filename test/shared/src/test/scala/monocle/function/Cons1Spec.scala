package monocle.function

import monocle.MonocleSuite
import monocle.law.discipline.function.Cons1Tests

import scala.{List => IList}

class Cons1Spec extends MonocleSuite {

  implicit val clistCons1: Cons1[CNel, Char, IList[Char]] = Cons1.fromIso(CNel.toNel)

  checkAll("fromIso", Cons1Tests[CNel, Char, IList[Char]])

}
