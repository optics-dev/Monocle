package monocle

import monocle.law.discipline.{LensTests, OptionalTests, SetterTests, TraversalTests}
import monocle.macros.{GenLens, Lenses}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._

import cats.Eq
import cats.arrow.{Category, Choice, Compose}

case class Point(x: Int, y: Int)
@Lenses case class Example(s: String, p: Point)
@Lenses case class Foo[A, B](q: Map[(A, B), Double], default: Double)

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

  val x  = Lens[Point, Int](_.x)(x => p => p.copy(x = x))
  val y  = Lens[Point, Int](_.y)(y => p => p.copy(y = y))
  val xy = Lens[Point, (Int, Int)](p => (p.x, p.y))(xy => p => p.copy(x = xy._1, y = xy._2))

  implicit val exampleGen: Arbitrary[Example] = Arbitrary(for {
    s <- arbitrary[String]
    x <- arbitrary[Int]
    y <- arbitrary[Int]
  } yield Example(s, Point(x, y)))

  implicit val exampleEq = Eq.fromUniversalEquals[Example]

  checkAll("apply Lens", LensTests(s))
  checkAll("GenLens", LensTests(GenLens[Example](_.s)))
  checkAll("GenLens chain", LensTests(GenLens[Example](_.p.x)))
  checkAll("Lenses", LensTests(Example.s))

  checkAll("lens.asOptional", OptionalTests(s.asOptional))
  checkAll("lens.asTraversal", TraversalTests(s.asTraversal))
  checkAll("lens.asSetter", SetterTests(s.asSetter))

  checkAll("first", LensTests(s.first[Boolean]))
  checkAll("second", LensTests(s.second[Boolean]))

  // test implicit resolution of type classes

  test("Lens has a Compose instance") {
    assertEquals(Compose[Lens].compose(x, p).get(Example("plop", Point(3, 4))), 3)
  }

  test("Lens has a Category instance") {
    assertEquals(Category[Lens].id[Int].get(3), 3)
  }

  test("Lens has a Choice instance") {
    assertEquals(Choice[Lens].choice(x, y).get(Right(Point(5, 6))), 6)
  }

  test("get") {
    assertEquals(x.get(Point(5, 2)), 5)
  }

  test("find") {
    assertEquals(x.find(_ > 5)(Point(9, 2)), Some(9))
    assertEquals(x.find(_ > 5)(Point(3, 2)), None)
  }

  test("exist") {
    assert(x.exist(_ > 5)(Point(9, 2)))
    assert(!x.exist(_ > 5)(Point(3, 2)))
  }

  test("set") {
    assertEquals(x.replace(5)(Point(9, 2)), Point(5, 2))
  }

  test("modify") {
    assertEquals(x.modify(_ + 1)(Point(9, 2)), Point(10, 2))
  }

  test("to") {
    assertEquals(x.to(_.toString()).get(Point(1, 2)), "1")
  }

  test("some") {
    case class SomeTest(x: Int, y: Option[Int])
    val obj = SomeTest(1, Some(2))

    val lens = GenLens[SomeTest](_.y)

    assertEquals(lens.some.getOption(obj), Some(2))
    assertEquals(obj.applyLens(lens).some.getOption, Some(2))
  }

  test("withDefault") {
    case class SomeTest(x: Int, y: Option[Int])
    val objSome = SomeTest(1, Some(2))
    val objNone = SomeTest(1, None)

    val lens = GenLens[SomeTest](_.y)

    assertEquals(lens.withDefault(0).get(objSome), 2)
    assertEquals(lens.withDefault(0).get(objNone), 0)

    assertEquals(objNone.applyLens(lens).withDefault(0).get, 0)
  }

  test("each") {
    case class SomeTest(x: Int, y: List[Int])
    val obj = SomeTest(1, List(1, 2, 3))

    val lens = GenLens[SomeTest](_.y)

    assertEquals(lens.each.getAll(obj), List(1, 2, 3))
    assertEquals(obj.applyLens(lens).each.getAll, List(1, 2, 3))
  }

}
