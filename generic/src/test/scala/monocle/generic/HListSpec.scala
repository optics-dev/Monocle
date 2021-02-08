package monocle.generic

import cats.Eq
import cats.implicits._
import monocle.law.discipline.IsoTests
import org.scalacheck.{Arbitrary, Cogen}
import shapeless.HList._
import shapeless.ops.hlist.{IsHCons, Init => HListInit}
import shapeless.{::, HNil}

import scala.annotation.nowarn
import munit.DisciplineSuite
import monocle.generic.all._
import monocle.function.all._

@nowarn
class HListSpec extends DisciplineSuite {
  case class Example(i: Int, b: Boolean, c: Char, f: Float, l: Long, d: Double)

  type H        = Int :: Boolean :: Char :: Float :: Long :: Double :: HNil
  type ReverseH = Double :: Long :: Float :: Char :: Boolean :: Int :: HNil

  val isHCons   = IsHCons[H]
  val hListinit = HListInit[H]

  type HTail = isHCons.T
  type HInit = hListinit.Out

  implicit val exampleEq  = Eq.fromUniversalEquals[Example]
  implicit val hEq        = Eq.instance[H]((a1, a2) => fromHList[H, Example].get(a1) === fromHList[H, Example].get(a2))
  implicit val reverseHEq = Eq.instance[ReverseH]((a1, a2) => a1.reverse === a2.reverse)
  implicit val hTailEq    = Eq.instance[HTail]((a1, a2) => (1 :: a1) === (1 :: a2))
  implicit val hInitEq    = Eq.instance[HInit]((a1, a2) => (a1.tail :+ 3.5) === (a2.tail :+ 3.5))

  implicit val exampleArb: Arbitrary[Example] = Arbitrary(for {
    i <- Arbitrary.arbitrary[Int]
    b <- Arbitrary.arbitrary[Boolean]
    c <- Arbitrary.arbitrary[Char]
    f <- Arbitrary.arbitrary[Float]
    l <- Arbitrary.arbitrary[Long]
    d <- Arbitrary.arbitrary[Double]
  } yield Example(i, b, c, f, l, d))
  implicit val example: Cogen[Example] =
    Cogen.tuple6[Int, Boolean, Char, Float, Long, Double].contramap[Example](e => (e.i, e.b, e.c, e.f, e.l, e.d))

  implicit val hArb          = Arbitrary(for { example <- Arbitrary.arbitrary[Example] } yield toHList[Example, H].get(example))
  implicit val reverseHArb   = Arbitrary(for { h <- Arbitrary.arbitrary[H] } yield h.reverse)
  implicit val hTailArb      = Arbitrary(for { h <- Arbitrary.arbitrary[H] } yield h.tail)
  implicit val hInitArb      = Arbitrary(for { h <- Arbitrary.arbitrary[H] } yield h.init)
  implicit val hCoGen        = Cogen[Example].contramap(fromHList[H, Example].get)
  implicit val reverseHCoGen = hCoGen.contramap[ReverseH](_.reverse)
  implicit val hTailCoGen    = Cogen.tuple5[Boolean, Char, Float, Long, Double].contramap[HTail](_.tupled)
  implicit val hInitCoGen    = Cogen.tuple5[Int, Boolean, Char, Float, Long].contramap[HInit](_.tupled)

  checkAll("toHList", IsoTests(toHList[Example, H]))

  checkAll("reverse HList", IsoTests(reverse[H, ReverseH])): @nowarn
  checkAll("hcons HList", IsoTests(cons1[H, Int, HTail])): @nowarn
  checkAll("hsnoc HList", IsoTests(snoc1[H, HInit, Double])): @nowarn
}
