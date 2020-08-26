package monocle.std

import cats.Eq
import monocle.MonocleSuite
import monocle.law.discipline.{IsoTests, PrismTests}
import monocle.law.discipline.function.{EachTests, EmptyTests, PossibleTests}
import org.scalacheck.{Arbitrary, Cogen}

class OptionSpec extends MonocleSuite {
  checkAll("some", PrismTests(some[Int]))
  checkAll("none", PrismTests(none[Long]))
  checkAll("optionToDisjunction", IsoTests(optionToDisjunction[Int]))
  checkAll("pOptionToDisjunction", IsoTests(pOptionToDisjunction[Int, Int]))

  checkAll("each Option", EachTests[Option[Int], Int])
  checkAll("possible Option", PossibleTests[Option[Int], Int])
  checkAll("empty Option", EmptyTests[Option[Int]])

  case class IntNoZero(value: Int)
  object IntNoZero {
    implicit val eq: Eq[IntNoZero] = Eq.fromUniversalEquals
    implicit val arbitrary: Arbitrary[IntNoZero] =
      Arbitrary(Arbitrary.arbitrary[Int].filterNot(_ == 0).map(IntNoZero(_)))
    implicit val cogen: Cogen[IntNoZero] =
      Cogen.cogenInt.contramap(_.value)
  }

  checkAll("withDefault Int 0", IsoTests(withDefault(IntNoZero(0))))
}
