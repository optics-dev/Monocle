package monocle.macros

import cats.Eq
import munit.DisciplineSuite
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import monocle.law.discipline._

class GenLensSpec extends DisciplineSuite {
  case class Point(x: Int, y: Int)
  case class Example(s: String, p: Point)

  implicit val exampleArb: Arbitrary[Example] = Arbitrary(for {
    s <- arbitrary[String]
    x <- arbitrary[Int]
    y <- arbitrary[Int]
  } yield Example(s, Point(x, y)))

  implicit val exampleEq: Eq[Example] = Eq.fromUniversalEquals[Example]

  checkAll("GenLens", LensTests(GenLens[Example](_.s)))
  checkAll("GenLens chain", LensTests(GenLens[Example](_.p.x)))

}
