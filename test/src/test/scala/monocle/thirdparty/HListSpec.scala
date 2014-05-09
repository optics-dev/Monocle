package monocle.thirdparty

import monocle.TestUtil._
import monocle.function.Fields._
import monocle.thirdparty.hlist._
import monocle.{IsoLaws, LensLaws}
import org.scalacheck.Arbitrary
import org.specs2.scalaz.Spec
import scalaz.Equal
import scalaz.syntax.equal._
import shapeless.{Generic, ::, HNil}

class HListSpec extends Spec {

  case class Example(i: Int, b: Boolean, c: Char, f: Float, l: Long, d: Double)

  type H = Int :: Boolean :: Char :: Float :: Long :: Double :: HNil

  implicit val exampleGen: Generic.Aux[Example, H] = Generic.product[Example]

  implicit val exampleEq = Equal.equalA[Example]

  implicit val hEq: Equal[H] = new Equal[H] {
    def equal(a1: H, a2: H): Boolean =
      fromHList[H, Example].get(a1) === fromHList[H,Example].get(a2)
  }

  implicit val exampleArb: Arbitrary[Example] = Arbitrary(for{
    i <- Arbitrary.arbitrary[Int]
    b <- Arbitrary.arbitrary[Boolean]
    c <- Arbitrary.arbitrary[Char]
    f <- Arbitrary.arbitrary[Float]
    l <- Arbitrary.arbitrary[Long]
    d <- Arbitrary.arbitrary[Double]
  } yield Example(i,b,c,f,l,d))

  implicit val hArb: Arbitrary[H] = Arbitrary(for {
    example <- Arbitrary.arbitrary[Example]
  } yield toHList[Example, H].get(example))


  checkAll("_1 from HList", LensLaws(_1[H, Int]))
  checkAll("_2 from HList", LensLaws(_2[H, Boolean]))
  checkAll("_3 from HList", LensLaws(_3[H, Char]))
  checkAll("_4 from HList", LensLaws(_4[H, Float]))
  checkAll("_5 from HList", LensLaws(_5[H, Long]))
  checkAll("_6 from HList", LensLaws(_6[H, Double]))

  checkAll("toHList", IsoLaws(toHList[Example, H]) )

}
