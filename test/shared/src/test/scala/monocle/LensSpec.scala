package monocle

import monocle.law.discipline.{LensTests, OptionalTests, SetterTests, TraversalTests}
import monocle.macros.{GenLens, Lenses}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._

import cats.{Eq => Equal}
import cats.arrow.{Category, Choice, Compose}
import scala.{Right => \/-}

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

class annot extends annotation.StaticAnnotation
@Lenses case class HasCompanion4(l: Long, s: String)
@annot object HasCompanion4

class LensSpec extends MonocleSuite {

  val s = Lens[Example, String](_.s)(s => ex => ex.copy(s = s))
  val p = Lens[Example, Point](_.p)(p => ex => ex.copy(p = p))

  val x = Lens[Point, Int](_.x)(x => p => p.copy(x = x))
  val y = Lens[Point, Int](_.y)(y => p => p.copy(y = y))
  val xy = Lens[Point, (Int, Int)](p => (p.x, p.y))(xy => p => p.copy(x = xy._1, y = xy._2))

  implicit val exampleGen: Arbitrary[Example] = Arbitrary(for {
    s <- arbitrary[String]
    x <- arbitrary[Int]
    y <- arbitrary[Int]
  } yield Example(s, Point(x, y)))

  implicit val exampleEq = Equal.fromUniversalEquals[Example]

  checkAll("apply Lens", LensTests(s))
  checkAll("GenLens", LensTests(GenLens[Example](_.s)))
  checkAll("GenLens chain", LensTests(GenLens[Example](_.p.x)))
  checkAll("Lenses",  LensTests(Example.s))

  checkAll("lens.asOptional" , OptionalTests(s.asOptional))
  checkAll("lens.asTraversal", TraversalTests(s.asTraversal))
  checkAll("lens.asSetter"   , SetterTests(s.asSetter))

  checkAll("first" ,  LensTests(s.first[Boolean]))
  checkAll("second",  LensTests(s.second[Boolean]))

  // test implicit resolution of type classes

  test("Lens has a Compose instance") {
    Compose[Lens].compose(x, p).get(Example("plop", Point(3, 4))) shouldEqual 3
  }

  test("Lens has a Category instance") {
    Category[Lens].id[Int].get(3) shouldEqual 3
  }

  test("Lens has a Choice instance") {
    Choice[Lens].choice(x, y).get(\/-(Point(5, 6))) shouldEqual 6
  }

  test("get") {
    x.get(Point(5, 2)) shouldEqual 5
  }

  test("find") {
    x.find(_ > 5)(Point(9, 2)) shouldEqual Some(9)
    x.find(_ > 5)(Point(3, 2)) shouldEqual None
  }

  test("exist") {
    x.exist(_ > 5)(Point(9, 2)) shouldEqual true
    x.exist(_ > 5)(Point(3, 2)) shouldEqual false
  }

  test("set") {
    x.set(5)(Point(9, 2)) shouldEqual Point(5, 2)
  }

  test("modify") {
    x.modify(_ + 1)(Point(9, 2)) shouldEqual Point(10, 2)
  }

}
