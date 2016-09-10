package monocle

import monocle.law.discipline.{SetterTests, TraversalTests}
import monocle.macros.GenLens
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

import scalaz._

class TraversalSpec extends MonocleSuite {

  case class Location(latitude: Int, longitude: Int, name: String)

  val coordinates: Traversal[Location, Int] = Traversal.apply2[Location, Int](_.latitude, _.longitude) {
    case (newLat, newLong, oldLoc) =>
      oldLoc.copy(latitude = newLat, longitude = newLong)
  }

  def all[A]: Traversal[IList[A], A] = PTraversal.fromTraverse[IList, A, A]

  implicit val locationGen: Arbitrary[Location] = Arbitrary(for {
    x <- arbitrary[Int]
    y <- arbitrary[Int]
    n <- arbitrary[String]
  } yield Location(x, y, n))

  implicit val exampleEq = Equal.equalA[Location]


  checkAll("apply2 Traversal", TraversalTests(coordinates))
  checkAll("fromTraverse Traversal" , TraversalTests(all[Int]))

  checkAll("traversal.asSetter", SetterTests(coordinates.asSetter))

  test("length") {
    all[Location].length(IList(Location(1,2,""), Location(3,4,""))) shouldEqual 2
    all[Location].length(INil[Location])                            shouldEqual 0
  }

  // test implicit resolution of type classes

  test("Traversal has a Compose instance") {
    Compose[Traversal].compose(coordinates, all[Location])
      .modify(_ + 1)(IList(Location(1,2,""), Location(3,4,""))) shouldEqual IList(Location(2,3,""), Location(4,5,""))
  }

  test("Traversal has a Category instance") {
    Category[Traversal].id[Int].getAll(3) shouldEqual List(3)
  }

  test("Traversal has a Choice instance") {
    Choice[Traversal].choice(all[Int], coordinates).modify(_ + 1)(-\/(IList(1,2,3))) shouldEqual -\/(IList(2,3,4))
  }



  // Below we test a 7-lenses Traversal created using applyN

  // the test object
  case class ManyPropObject(p1: Int, p2: Int, p3: String, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int)

  // the 7 lenses for each int properties of the test object
  val l1: Lens[ManyPropObject,Int] = GenLens[ManyPropObject](_.p1)
  val l2: Lens[ManyPropObject,Int] = GenLens[ManyPropObject](_.p2)
  val l3: Lens[ManyPropObject,Int] = GenLens[ManyPropObject](_.p4)
  val l4: Lens[ManyPropObject,Int] = GenLens[ManyPropObject](_.p5)
  val l5: Lens[ManyPropObject,Int] = GenLens[ManyPropObject](_.p6)
  val l6: Lens[ManyPropObject,Int] = GenLens[ManyPropObject](_.p7)
  val l7: Lens[ManyPropObject,Int] = GenLens[ManyPropObject](_.p8)

  // the 7-lenses Traversal generated using applyN
  val traversalN: Traversal[ManyPropObject, Int] = Traversal.applyN(l1,l2,l3,l4,l5,l6,l7)

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
  } yield ManyPropObject(p1,p2,p3,p4,p5,p6,p7,p8))

  implicit val eqForManyPropObject = Equal.equalA[ManyPropObject]

  checkAll("applyN Traversal", TraversalTests(traversalN))

}
