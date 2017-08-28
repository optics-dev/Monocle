package monocle.refined

import eu.timepit.refined.scalacheck.char._
import eu.timepit.refined.scalacheck.refTypeCogen
import monocle._
import monocle.law.discipline.PrismTests

import cats.{Eq => Equal}

class CharSpec extends MonocleSuite {

  implicit val eqLowerCase: Equal[LowerCaseChar] = Equal.fromUniversalEquals[LowerCaseChar]
  implicit val eqUpperCase: Equal[UpperCaseChar] = Equal.fromUniversalEquals[UpperCaseChar]

  checkAll("lower cases", PrismTests(lowerCase))
  checkAll("upper cases", PrismTests(upperCase))

}
