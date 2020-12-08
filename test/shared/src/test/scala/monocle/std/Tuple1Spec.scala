package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.IsoTests
import monocle.law.discipline.function.ReverseTests
import org.scalacheck.Arbitrary

import scala.annotation.nowarn

class Tuple1Spec extends MonocleSuite {
  implicit def arbitraryTuple1[T](implicit arb: Arbitrary[T]): Arbitrary[Tuple1[T]] =
    Arbitrary(arb.arbitrary.map(Tuple1.apply))

  checkAll("reverse tuple1", ReverseTests[Tuple1[Int], Tuple1[Int]]): @nowarn
  checkAll("tuple1 iso", IsoTests[Tuple1[Int], Int](tuple1Iso))
}
