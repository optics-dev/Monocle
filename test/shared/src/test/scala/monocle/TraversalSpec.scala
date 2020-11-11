package monocle

import monocle.law.discipline.{SetterTests, TraversalTests}
import monocle.macros.GenLens
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

import cats.Eq
import cats.arrow.{Category, Choice, Compose}
import cats.syntax.either._

class TraversalSpec extends MonocleSuite {
  case class Location(latitude: Int, longitude: Int, name: String)

  val coordinates: Traversal[Location, Int] =
    Traversal.apply2[Location, Int](_.latitude, _.longitude) { case (newLat, newLong, oldLoc) =>
      oldLoc.copy(latitude = newLat, longitude = newLong)
    }

  def eachL[A]: Traversal[List[A], A]               = PTraversal.fromTraverse[List, A, A]
  val eachLi: Traversal[List[Int], Int]             = eachL[Int]
  def eachL2[A, B]: Traversal[List[(A, B)], (A, B)] = eachL[(A, B)]

  implicit val locationGen: Arbitrary[Location] = Arbitrary(for {
    x <- arbitrary[Int]
    y <- arbitrary[Int]
    n <- arbitrary[String]
  } yield Location(x, y, n))

  implicit val exampleEq = Eq.fromUniversalEquals[Location]

  // Below we test a 7-lenses Traversal created using applyN

  // the test object
  case class ManyPropObject(p1: Int, p2: Int, p3: String, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int)

  // the 7 lenses for each int properties of the test object
  val l1: Lens[ManyPropObject, Int] = GenLens[ManyPropObject](_.p1)
  val l2: Lens[ManyPropObject, Int] = GenLens[ManyPropObject](_.p2)
  val l3: Lens[ManyPropObject, Int] = GenLens[ManyPropObject](_.p4)
  val l4: Lens[ManyPropObject, Int] = GenLens[ManyPropObject](_.p5)
  val l5: Lens[ManyPropObject, Int] = GenLens[ManyPropObject](_.p6)
  val l6: Lens[ManyPropObject, Int] = GenLens[ManyPropObject](_.p7)
  val l7: Lens[ManyPropObject, Int] = GenLens[ManyPropObject](_.p8)

  // the 7-lenses Traversal generated using applyN
  val traversalN: Traversal[ManyPropObject, Int] =
    Traversal.applyN(l1, l2, l3, l4, l5, l6, l7)

  // the stub for generating random test objects
  implicit val manyPropObjectGen: Arbitrary[ManyPropObject] = Arbitrary(for {
    p1 <- arbitrary[Int]
    p2 <- arbitrary[Int]
    p3 <- arbitrary[String]
    p4 <- arbitrary[Int]
    p5 <- arbitrary[Int]
    p6 <- arbitrary[Int]
    p7 <- arbitrary[Int]
    p8 <- arbitrary[Int]
  } yield ManyPropObject(p1, p2, p3, p4, p5, p6, p7, p8))

  implicit val eqForManyPropObject = Eq.fromUniversalEquals[ManyPropObject]

  checkAll("apply2 Traversal", TraversalTests(coordinates))
  checkAll("applyN Traversal", TraversalTests(traversalN))
  checkAll("fromTraverse Traversal", TraversalTests(eachLi))

  checkAll("traversal.asSetter", SetterTests(coordinates.asSetter))

  // test implicit resolution of type classes

  test("Traversal has a Compose instance") {
    assertEquals(
      Compose[Traversal]
        .compose(coordinates, eachL[Location])
        .modify(_ + 1)(List(Location(1, 2, ""), Location(3, 4, ""))),
      List(Location(2, 3, ""), Location(4, 5, ""))
    )
  }

  test("Traversal has a Category instance") {
    assertEquals(Category[Traversal].id[Int].getAll(3), List(3))
  }

  test("Traversal has a Choice instance") {
    assertEquals(
      Choice[Traversal]
        .choice(eachL[Int], coordinates)
        .modify(_ + 1)(Left(List(1, 2, 3))),
      Left(List(2, 3, 4))
    )
  }

  test("foldMap") {
    assertEquals(eachLi.foldMap(_.toString)(List(1, 2, 3, 4, 5)), "12345")
  }

  test("getAll") {
    assertEquals(eachLi.getAll(List(1, 2, 3, 4)), List(1, 2, 3, 4))
  }

  test("headOption") {
    assertEquals(eachLi.headOption(List(1, 2, 3, 4)), Some(1))
  }

  test("lastOption") {
    assertEquals(eachLi.lastOption(List(1, 2, 3, 4)), Some(4))
  }

  test("length") {
    assertEquals(eachLi.length(List(1, 2, 3, 4)), 4)
    assertEquals(eachLi.length(Nil), 0)
  }

  test("isEmpty") {
    assertEquals(eachLi.isEmpty(List(1, 2, 3, 4)), false)
    assertEquals(eachLi.isEmpty(Nil), true)
  }

  test("nonEmpty") {
    assertEquals(eachLi.nonEmpty(List(1, 2, 3, 4)), true)
    assertEquals(eachLi.nonEmpty(Nil), false)
  }

  test("find") {
    assertEquals(eachLi.find(_ > 2)(List(1, 2, 3, 4)), Some(3))
    assertEquals(eachLi.find(_ > 9)(List(1, 2, 3, 4)), None)
  }

  test("exist") {
    assertEquals(eachLi.exist(_ > 2)(List(1, 2, 3, 4)), true)
    assertEquals(eachLi.exist(_ > 9)(List(1, 2, 3, 4)), false)
    assertEquals(eachLi.exist(_ > 9)(Nil), false)
  }

  test("all") {
    assertEquals(eachLi.all(_ > 2)(List(1, 2, 3, 4)), false)
    assertEquals(eachLi.all(_ > 0)(List(1, 2, 3, 4)), true)
    assertEquals(eachLi.all(_ > 0)(Nil), true)
  }

  test("set") {
    assertEquals(eachLi.set(0)(List(1, 2, 3, 4)), List(0, 0, 0, 0))
  }

  test("modify") {
    assertEquals(eachLi.modify(_ + 1)(List(1, 2, 3, 4)), List(2, 3, 4, 5))
  }

  test("parModifyF") {
    assertEquals(
      eachLi.parModifyF[Either[Unit, *]](i => (i + 1).asRight[Unit])(List(1, 2, 3, 4)),
      Right(List(2, 3, 4, 5))
    )
    // `Left` values should be accumulated through `Validated`.
    assertEquals(eachLi.parModifyF[Either[String, *]](_.toString.asLeft[Int])(List(1, 2, 3, 4)), Left("1234"))
  }

  test("to") {
    assertEquals(eachLi.to(_.toString()).getAll(List(1, 2, 3)), List("1", "2", "3"))
  }

  test("some") {
    val numbers   = List(Some(1), None, Some(2), None)
    val traversal = Traversal.fromTraverse[List, Option[Int]]

    assertEquals(traversal.some.set(5)(numbers), List(Some(5), None, Some(5), None))
    assertEquals(numbers.applyTraversal(traversal).some.set(5), List(Some(5), None, Some(5), None))
  }

  test("withDefault") {
    val numbers   = List(Some(1), None, Some(2), None)
    val traversal = Traversal.fromTraverse[List, Option[Int]]

    assertEquals(traversal.withDefault(0).modify(_ + 1)(numbers), List(Some(2), Some(1), Some(3), Some(1)))
    assertEquals(
      numbers.applyTraversal(traversal).withDefault(0).modify(_ + 1),
      List(Some(2), Some(1), Some(3), Some(1))
    )
  }

  test("each") {
    val numbers   = List(List(1, 2, 3), Nil, List(4), Nil)
    val traversal = Traversal.fromTraverse[List, List[Int]]

    assertEquals(traversal.each.getAll(numbers), List(1, 2, 3, 4))
    assertEquals(numbers.applyTraversal(traversal).each.getAll, List(1, 2, 3, 4))
  }
}
