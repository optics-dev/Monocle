package monocle.generic

import monocle.MonocleSuite
import monocle.law.discipline.{PrismTests, IsoTests}
import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import shapeless.{:+:, CNil, Coproduct, Inl, Inr}

import scalaz.Equal

class CoproductSpec extends MonocleSuite {

  type IB = Int :+: Boolean :+: CNil

  implicit val isbArbitrary = Arbitrary( Gen.oneOf(
    arbitrary[Int].map(Coproduct[IB](_)),
    arbitrary[Boolean].map(Coproduct[IB](_))
  ))

  implicit val isbEqual = new Equal[IB]{
    override def equal(a1: IB, a2: IB): Boolean = (a1, a2) match {
      case (Inl(i1), Inl(i2)) => i1 == i2
      case (Inr(b1), Inr(b2)) => b1 == b2
      case _                  => false
    }
  }

  checkAll("Coproduct Prism", PrismTests(coProductPrism[IB, Boolean]))

  checkAll("Coproduct Iso", IsoTests(coProductIso[IB].apply))

  checkAll("Coproduct Disjunction Iso", IsoTests(coProductDisjunctionIso[IB].apply))
}
