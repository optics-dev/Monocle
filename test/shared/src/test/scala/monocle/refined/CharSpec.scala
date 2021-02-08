package monocle.refined

import eu.timepit.refined.scalacheck.char._
import eu.timepit.refined.scalacheck.refTypeCogen
import eu.timepit.refined.scalacheck.any.arbitraryFromValidate
import monocle._
import monocle.law.discipline.PrismTests

import cats.Eq

class CharSpec extends MonocleSuite {
  implicit val eqLowerCase: Eq[LowerCaseChar] = Eq.fromUniversalEquals[LowerCaseChar]
  implicit val eqUpperCase: Eq[UpperCaseChar] = Eq.fromUniversalEquals[UpperCaseChar]

  checkAll("lower cases", PrismTests(lowerCase))
  checkAll("upper cases", PrismTests(upperCase))
}
