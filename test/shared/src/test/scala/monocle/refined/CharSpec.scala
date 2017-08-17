package monocle.refined

import eu.timepit.refined.scalacheck.char._
import eu.timepit.refined.scalacheck.refTypeCogen
import monocle._
import monocle.law.discipline.PrismTests

import scalaz.Equal

class CharSpec extends MonocleSuite {
  implicit val eqLowerCase: Equal[LowerCaseChar] = Equal.equalA[LowerCaseChar]
  implicit val eqUpperCase: Equal[UpperCaseChar] = Equal.equalA[UpperCaseChar]
  implicit val eqChar: Equal[Char] = Equal.equalA[Char]

  checkAll("lower cases", PrismTests(lowerCase))
  checkAll("upper cases", PrismTests(upperCase))

}
