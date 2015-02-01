package monocle

import monocle.TestUtil._
import monocle.law.{LensLaws, OptionalLaws, SetterLaws, TraversalLaws}
import monocle.macros.{GenLens, Lenses}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.specs2.scalaz._

import scalaz._

class LensSpec extends Spec {

  case class Point(x: Int, y: Int)

  @Lenses
  case class Example(s: String, p: Point)

  val _s = Lens[Example, String](_.s)(s => ex => ex.copy(s = s))
  val _p = Lens[Example, Point](_.p)(p => ex => ex.copy(p = p))

  val _x = Lens[Point, Int](_.x)(x => p => p.copy(x = x))
  val _y = Lens[Point, Int](_.y)(y => p => p.copy(y = y))

  implicit val exampleGen: Arbitrary[Example] = Arbitrary(for {
    s <- arbitrary[String]
    x <- arbitrary[Int]
    y <- arbitrary[Int]
  } yield Example(s, Point(x, y)))

  implicit val exampleEq = Equal.equalA[Example]

  checkAll("apply Lens", LensLaws(_s))
  checkAll("GenLens", LensLaws(GenLens[Example](_.s)))
  checkAll("Lenses",  LensLaws(Example.s))

  checkAll("lens.asOptional" , OptionalLaws(_s.asOptional))
  checkAll("lens.asTraversal", TraversalLaws(_s.asTraversal))
  checkAll("lens.asSetter"   , SetterLaws(_s.asSetter))

  // test implicit resolution of type classes

  "Lens has a Compose instance" in {
    Compose[Lens].compose(_x, _p).get(Example("plop", Point(3, 4))) ==== 3
  }

  "Lens has a Category instance" in {
    Category[Lens].id[Int].get(3) ==== 3
  }

  "Lens has a Choice instance" in {
    Choice[Lens].choice(_x, _y).get(\/-(Point(5, 6))) ==== 6
  }

  "Lens has a Split instance" in {
    Split[Lens].split(_x, _y).get((Point(0, 1), Point(5, 6))) ==== ((0, 6))
  }

}
