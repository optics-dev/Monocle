package monocle.macros

import cats.Eq
import monocle.Iso
import monocle.law.discipline._
import munit.DisciplineSuite
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary

object GenIsoSpec {
  case object CaseObj
  implicit val genCaseObj: Arbitrary[CaseObj.type] = Arbitrary(Gen.const(CaseObj))
  implicit val eqCaseObj: Eq[CaseObj.type]         = Eq.fromUniversalEquals

  case class Zero()
  implicit val genZero: Arbitrary[Zero] = Arbitrary(Gen.const(Zero()))
  implicit val eqZero: Eq[Zero]         = Eq.fromUniversalEquals

  case class ZeroT[A]()
  implicit def genZeroT[A]: Arbitrary[ZeroT[A]] = Arbitrary(Gen.const(ZeroT()))
  implicit def eqZeroT[A]: Eq[ZeroT[A]] = Eq.fromUniversalEquals

  case class One(i: Int)
  implicit val genOne: Arbitrary[One] = Arbitrary(arbitrary[Int].map(One.apply))
  implicit val eqOne: Eq[One]         = Eq.fromUniversalEquals

  case class OneT[A](value: A)
  implicit def genOneT[A: Arbitrary]: Arbitrary[OneT[A]] = Arbitrary(arbitrary[A].map(OneT.apply))
  implicit def eqOneT[A: Eq]: Eq[OneT[A]]                = Eq.fromUniversalEquals

  case class Two(i: Int, l: Short)
  implicit val genTwo: Arbitrary[Two] = Arbitrary(arbitrary[Int].flatMap(i => arbitrary[Short].map(Two(i, _))))
  implicit val eqTwo: Eq[Two]         = Eq.fromUniversalEquals

  case class TwoT[A, B](i: A, l: B)
  implicit def genTwoT[A: Arbitrary, B: Arbitrary]: Arbitrary[TwoT[A, B]] = Arbitrary(arbitrary[A].flatMap(a => arbitrary[B].map(TwoT(a, _))))
  implicit def eqTwoT[A, B]: Eq[TwoT[A, B]] = Eq.fromUniversalEquals
}

class GenIsoSpec extends DisciplineSuite {
  import GenIsoSpec._

  trait CompileTimeTests {
    locally { type S = One             ; val x = GenIso.apply[S, Int]; x: Iso[S, Int] }
    locally { type S = OneT[Int]       ; val x = GenIso.apply[S, Int]; x: Iso[S, Int] }

    locally { type S = CaseObj.type    ; val x = GenIso.fields[S]; x: Iso[S, Unit] }
    locally { type S = Zero            ; val x = GenIso.fields[S]; x: Iso[S, Unit] }
    locally { type S = ZeroT[Int]      ; val x = GenIso.fields[S]; x: Iso[S, Unit] }
    locally { type S = One             ; val x = GenIso.fields[S]; x: Iso[S, Int] }
    locally { type S = OneT[Int]       ; val x = GenIso.fields[S]; x: Iso[S, Int] }
    locally { type S = Two             ; val x = GenIso.fields[S]; x: Iso[S, (Int, Short)] }
    locally { type S = TwoT[Int, Short]; val x = GenIso.fields[S]; x: Iso[S, (Int, Short)] }

    locally { type S = CaseObj.type    ; val x = GenIso.unit[S]; x: Iso[S, Unit] }
    locally { type S = Zero            ; val x = GenIso.unit[S]; x: Iso[S, Unit] }
    locally { type S = ZeroT[Int]      ; val x = GenIso.unit[S]; x: Iso[S, Unit] }
  }

  checkAll("GenIso.apply[One, Int]"              , IsoTests(GenIso.apply[One, Int]))
  checkAll("GenIso.apply[OneT[Int], Int]"        , IsoTests(GenIso.apply[OneT[Int], Int]))
  checkAll("GenIso.fields[CaseObj.type]"         , IsoTests(GenIso.fields[CaseObj.type]))
  checkAll("GenIso.fields[Zero]"                 , IsoTests(GenIso.fields[Zero]))
  checkAll("GenIso.fields[ZeroT[Int]]"           , IsoTests(GenIso.fields[ZeroT[Int]]))
  checkAll("GenIso.fields[One]"                  , IsoTests(GenIso.fields[One]))
  checkAll("GenIso.fields[OneT[Int]]"            , IsoTests(GenIso.fields[OneT[Int]]))
  checkAll("GenIso.fields[Two]"                  , IsoTests(GenIso.fields[Two]))
  checkAll("GenIso.fields[TwoT[Int, Short]]"     , IsoTests(GenIso.fields[TwoT[Int, Short]]))
  checkAll("GenIso.unit[CaseObj.type]"           , IsoTests(GenIso.unit[CaseObj.type]))
  checkAll("GenIso.unit[Zero]"                   , IsoTests(GenIso.unit[Zero]))
  checkAll("GenIso.unit[ZeroT[Int]]"             , IsoTests(GenIso.unit[ZeroT[Int]]))
}
