package monocle.refined

import eu.timepit.refined.api.Refined
import monocle._
import monocle.law.discipline.PrismTests
import org.scalacheck.{Arbitrary, Cogen, Gen}

import cats.{Eq => Equal}

class CharSpec extends MonocleSuite {
  implicit val lowerCaseRefinedCharArb: Arbitrary[LowerCaseChar] =
    Arbitrary(Gen.alphaLowerChar.map(Refined.unsafeApply))

  implicit val upperCaseRefinedCharArb: Arbitrary[UpperCaseChar] =
    Arbitrary(Gen.alphaUpperChar.map(Refined.unsafeApply))

  implicit val lowerCaseCoGen: Cogen[LowerCaseChar] = Cogen[Char].contramap[LowerCaseChar](_.value)
  implicit val upperCaseCoGen: Cogen[UpperCaseChar] = Cogen[Char].contramap[UpperCaseChar](_.value)

  implicit val eqLowerCase: Equal[LowerCaseChar] = Equal.fromUniversalEquals[LowerCaseChar]
  implicit val eqUpperCase: Equal[UpperCaseChar] = Equal.fromUniversalEquals[UpperCaseChar]

  checkAll("lower cases", PrismTests(lowerCase))
  checkAll("upper cases", PrismTests(upperCase))

}
