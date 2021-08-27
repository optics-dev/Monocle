package monocle.macros

import cats.Eq
import monocle.Iso
import monocle.law.discipline._
import munit.DisciplineSuite
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary

class GenIsoScala3Spec extends DisciplineSuite {
  import GenIsoSpec._

  implicit val genEmptyTupleF: Arbitrary[EmptyTuple => EmptyTuple] = Arbitrary(Gen.const(_ => EmptyTuple))
  implicit val genEmptyTuple: Arbitrary[EmptyTuple]                = Arbitrary(Gen.const(EmptyTuple))
  implicit val eqEmptyTuple: Eq[EmptyTuple]                        = Eq.fromUniversalEquals

  trait CompileTimeTests {
    locally { type S = CaseObj.type; val x = GenIso.fieldsTuple[S]; x: Iso[S, EmptyTuple] }
    locally { type S = Zero; val x = GenIso.fieldsTuple[S]; x: Iso[S, EmptyTuple] }
    locally { type S = ZeroT[Int]; val x = GenIso.fieldsTuple[S]; x: Iso[S, EmptyTuple] }
    locally { type S = One; val x = GenIso.fieldsTuple[S]; x: Iso[S, Tuple1[Int]] }
    locally { type S = OneT[Int]; val x = GenIso.fieldsTuple[S]; x: Iso[S, Tuple1[Int]] }
    locally { type S = Two; val x = GenIso.fieldsTuple[S]; x: Iso[S, (Int, Short)] }
    locally { type S = TwoT[Int, Short]; val x = GenIso.fieldsTuple[S]; x: Iso[S, (Int, Short)] }
  }

  checkAll("GenIso.fieldsTuple[CaseObj.type]", IsoTests(GenIso.fieldsTuple[CaseObj.type]))
  checkAll("GenIso.fieldsTuple[Zero]", IsoTests(GenIso.fieldsTuple[Zero]))
  checkAll("GenIso.fieldsTuple[ZeroT[Int]]", IsoTests(GenIso.fieldsTuple[ZeroT[Int]]))
  checkAll("GenIso.fieldsTuple[One]", IsoTests(GenIso.fieldsTuple[One]))
  checkAll("GenIso.fieldsTuple[OneT[Int]]", IsoTests(GenIso.fieldsTuple[OneT[Int]]))
  checkAll("GenIso.fieldsTuple[Two]", IsoTests(GenIso.fieldsTuple[Two]))
  checkAll("GenIso.fieldsTuple[TwoT[Int, Short]]", IsoTests(GenIso.fieldsTuple[TwoT[Int, Short]]))
}
