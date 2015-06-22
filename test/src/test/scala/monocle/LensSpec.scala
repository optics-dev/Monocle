package monocle

import monocle.law.discipline.{LensTests, OptionalTests, SetterTests, TraversalTests}
import monocle.macros.{GenLens, Lenses}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._

import scalaz._

case class Point(x: Int, y: Int)
@Lenses case class Example(s: String, p: Point)
@Lenses case class Foo[A,B](q: Map[(A,B),Double], default: Double)

// a few more examples that should compile
@Lenses case class HasCompanion1[A](a: A)
object HasCompanion1

@Lenses case class HasCompanion2[A](a: A)
object HasCompanion2 { def foo = () }

trait Bar; trait Baz
@Lenses case class HasCompanion3[A](a: A)
object HasCompanion3 extends Bar with Baz

class LensSpec extends MonocleSuite {

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

  checkAll("apply Lens", LensTests(_s))
  checkAll("GenLens", LensTests(GenLens[Example](_.s)))
  checkAll("GenLens chain", LensTests(GenLens[Example](_.p.x)))
  checkAll("Lenses",  LensTests(Example.s))

  checkAll("lens.asOptional" , OptionalTests(_s.asOptional))
  checkAll("lens.asTraversal", TraversalTests(_s.asTraversal))
  checkAll("lens.asSetter"   , SetterTests(_s.asSetter))

  // test implicit resolution of type classes

  test("Lens has a Compose instance") {
    Compose[Lens].compose(_x, _p).get(Example("plop", Point(3, 4))) shouldEqual 3
  }

  test("Lens has a Category instance") {
    Category[Lens].id[Int].get(3) shouldEqual 3
  }

  test("Lens has a Choice instance") {
    Choice[Lens].choice(_x, _y).get(\/-(Point(5, 6))) shouldEqual 6
  }

  test("Lens has a Split instance") {
    Split[Lens].split(_x, _y).get((Point(0, 1), Point(5, 6))) shouldEqual ((0, 6))
  }
  
}
