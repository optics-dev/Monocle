package monocle.generic

import monocle.MonocleSuite
import monocle.law.discipline.{PrismTests, IsoTests}
import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Cogen, Gen}
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

  checkAll("Coproduct Either Iso", IsoTests(coProductEitherIso[IB].apply))

  checkAll("Coproduct Disjunction Iso", IsoTests(coProductDisjunctionIso[IB].apply))

  sealed trait S
  case class A() extends S
  case object B extends S

  implicit val sArbitrary = Arbitrary[S](Gen.oneOf(A(), B))
  implicit val sEqual = Equal.equal[S](_ == _)

  implicit val aArb: Arbitrary[A] = Arbitrary(A())
  implicit val aEq: Equal[A]      = Equal.equal(_ == _)
  implicit val aCogen: Cogen[A]   = Cogen(_ => 1)

  implicit val bArb: Arbitrary[B.type] = Arbitrary(B)
  implicit val bEq: Equal[B.type]      = Equal.equal(_ == _)
  implicit val bCogen: Cogen[B.type]   = Cogen(_ => 2)

  checkAll("Coproduct To Either", IsoTests(coProductToEither[S].apply))

  checkAll("Coproduct To Disjunction", IsoTests(coProductToDisjunction[S].apply))
}
