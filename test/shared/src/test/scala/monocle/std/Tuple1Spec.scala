package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.{IsoTests, LensTests}
import monocle.law.discipline.function.ReverseTests
import org.scalacheck.Arbitrary
import scalaz.Equal

class Tuple1Spec extends MonocleSuite {
  implicit def arbitraryTuple1[T](implicit arb: Arbitrary[T]): Arbitrary[Tuple1[T]] =
    Arbitrary(arb.arbitrary.map(Tuple1.apply))

  implicit def equalTuple1[T](implicit eql: Equal[T]): Equal[Tuple1[T]] =
    new Equal[Tuple1[T]]{
      def equal(x: Tuple1[T], y: Tuple1[T]): Boolean = eql.equal(x._1, y._1)
    }

  checkAll("first tuple1", LensTests(first[Tuple1[Int], Int]))
  checkAll("reverse tuple1", ReverseTests[Tuple1[Int], Tuple1[Int]])
  checkAll("tuple1 iso", IsoTests[Tuple1[Int], Int](tuple1Iso))
}
