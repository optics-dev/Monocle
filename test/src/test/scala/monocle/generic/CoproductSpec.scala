package monocle.generic

import monocle.PrismLaws
import monocle.TestUtil._
import monocle.function.SafeCast._
import org.scalacheck.Arbitrary._
import org.scalacheck.{Gen, Arbitrary}
import org.specs2.scalaz.Spec
import scalaz.Equal
import shapeless.{:+:, CNil, Coproduct, Inl, Inr}


class CoproductSpec extends Spec {

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

  checkAll("safeCast Coproduct", PrismLaws(safeCast[IB, Boolean]))

}
