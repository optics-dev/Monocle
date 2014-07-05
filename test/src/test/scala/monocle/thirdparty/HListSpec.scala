package monocle.thirdparty

import monocle.TestUtil._
import monocle.function.Fields._
import monocle.function.Init._
import monocle.function.Head._
import monocle.function.Last._
import monocle.function.Reverse._
import monocle.function.Tail._
import monocle.thirdparty.hlist._
import monocle.{IsoLaws, LensLaws}
import org.scalacheck.Arbitrary
import org.specs2.scalaz.Spec
import scalaz.Equal
import scalaz.syntax.equal._
import shapeless.HList._
import shapeless.{Generic, ::, HNil}
import shapeless.ops.hlist.{IsHCons, Init => HListInit}

class HListSpec extends Spec {

  case class Example(i: Int, b: Boolean, c: Char, f: Float, l: Long, d: Double)

  type H        = Int :: Boolean :: Char :: Float :: Long :: Double :: HNil
  type ReverseH = Double :: Long :: Float :: Char :: Boolean :: Int :: HNil

  val isHCons   = IsHCons[H]
  val hListinit = HListInit[H]

  type HTail    = isHCons.T
  type HInit    = hListinit.Out


  implicit val exampleGen: Generic.Aux[Example, H] = Generic.product[Example]

  implicit val exampleEq  = Equal.equalA[Example]
  implicit val hEq        = Equal.equal[H]((a1, a2) => fromHList[H, Example].get(a1) === fromHList[H, Example].get(a2))
  implicit val reverseHEq = Equal.equal[ReverseH]((a1, a2) => a1.reverse === a2.reverse)
  implicit val hTailEq    = Equal.equal[HTail]((a1, a2) => (1 :: a1) === (1 :: a2))
  implicit val hInitEq    = Equal.equal[HInit]((a1, a2) => (a1.tail :+ 3.5) === (a2.tail :+ 3.5))

  implicit val exampleArb: Arbitrary[Example] = Arbitrary(for{
    i <- Arbitrary.arbitrary[Int]
    b <- Arbitrary.arbitrary[Boolean]
    c <- Arbitrary.arbitrary[Char]
    f <- Arbitrary.arbitrary[Float]
    l <- Arbitrary.arbitrary[Long]
    d <- Arbitrary.arbitrary[Double]
  } yield Example(i,b,c,f,l,d))

  implicit val hArb        = Arbitrary(for {example <- Arbitrary.arbitrary[Example]} yield toHList[Example, H].get(example))
  implicit val reverseHArb = Arbitrary(for {h <- Arbitrary.arbitrary[H]} yield h.reverse)
  implicit val hTailArb    = Arbitrary(for {h <- Arbitrary.arbitrary[H]} yield h.tail)
  implicit val hInitArb    = Arbitrary(for {h <- Arbitrary.arbitrary[H]} yield h.init)


  checkAll("first from HList", LensLaws(first[H, Int]))
  checkAll("second from HList", LensLaws(second[H, Boolean]))
  checkAll("third from HList", LensLaws(third[H, Char]))
  checkAll("fourth from HList", LensLaws(fourth[H, Float]))
  checkAll("fifth from HList", LensLaws(fifth[H, Long]))
  checkAll("sixth from HList", LensLaws(sixth[H, Double]))

  checkAll("toHList"      , IsoLaws(toHList[Example, H]))
  checkAll("reverse HList", IsoLaws(reverse[H, ReverseH]))
  checkAll("head from HList", LensLaws(head[H, Int]))
  checkAll("last from HList", LensLaws(last[H, Double]))
  checkAll("tail from HList", LensLaws(tail[H, HTail]))
  checkAll("init from HList", LensLaws(init[H, HInit]))

}
