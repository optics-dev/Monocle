package monocle.refined

import eu.timepit.refined.scalacheck.char._
import eu.timepit.refined.scalacheck.refTypeCogen
import monocle.refined.all._
import monocle.law.discipline.PrismTests

import cats.Eq

class CharSpec extends munit.DisciplineSuite {
  implicit val eqLowerCase: Eq[LowerCaseChar] = Eq.fromUniversalEquals[LowerCaseChar]
  implicit val eqUpperCase: Eq[UpperCaseChar] = Eq.fromUniversalEquals[UpperCaseChar]

  checkAll("lower cases", PrismTests(lowerCase))
  checkAll("upper cases", PrismTests(upperCase))
}
