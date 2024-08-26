package monocle.macros

import cats.Eq
import munit.DisciplineSuite
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Cogen, Gen}
import monocle.law.discipline._

class GenPrismSpec extends DisciplineSuite {
  sealed trait IntOrString
  case class I(i: Int)    extends IntOrString
  case class S(s: String) extends IntOrString

  implicit val iArb: Arbitrary[I] = Arbitrary(arbitrary[Int].map(I.apply))
  implicit val sArb: Arbitrary[S] = Arbitrary(arbitrary[String].map(S.apply))

  implicit val intOrStringArb: Arbitrary[IntOrString] =
    Arbitrary(Gen.oneOf(iArb.arbitrary, sArb.arbitrary))

  implicit val intOrStringEq: Eq[IntOrString] = Eq.fromUniversalEquals[IntOrString]
  implicit val iEq: Eq[I]                     = Eq.fromUniversalEquals[I]
  implicit val sEq: Eq[S]                     = Eq.fromUniversalEquals[S]

  implicit val iCogen: Cogen[I] = Cogen.cogenInt.contramap(_.i)
  implicit val sCogen: Cogen[S] = Cogen.cogenString.contramap(_.s)

  checkAll("GenPrism I", PrismTests(GenPrism[IntOrString, I]))
  checkAll("GenPrism S", PrismTests(GenPrism[IntOrString, S]))

}
