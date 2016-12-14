package monocle.refined

import eu.timepit.refined.api.Refined
import monocle._
import monocle.law.discipline.PrismTests
import org.scalacheck.{Arbitrary, Cogen, Gen}

import scalaz.Equal

class CharSpec extends MonocleSuite {
  implicit val lowerCaseRefinedCharArb: Arbitrary[LowerCaseChar] = Arbitrary(
    Gen.alphaLowerChar.map(Refined.unsafeApply)
  )

  implicit val upperCaseRefinedCharArb: Arbitrary[UpperCaseChar] = Arbitrary(
    Gen.alphaUpperChar.map(Refined.unsafeApply)
  )

  implicit val lowerCaseCoGen: Cogen[LowerCaseChar] = Cogen[Char].contramap[LowerCaseChar](_.get)
  implicit val upperCaseCoGen: Cogen[UpperCaseChar] = Cogen[Char].contramap[UpperCaseChar](_.get)

  implicit val eqLowerCase: Equal[LowerCaseChar] = Equal.equalA[LowerCaseChar]
  implicit val eqUpperCase: Equal[UpperCaseChar] = Equal.equalA[UpperCaseChar]
  implicit val eqChar: Equal[Char] = Equal.equalA[Char]

  checkAll("lower cases", PrismTests(lowerCase))
  checkAll("upper cases", PrismTests(upperCase))

}
