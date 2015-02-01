package monocle

import monocle.TestUtil._
import monocle.law.{SetterLaws, TraversalLaws}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.specs2.scalaz.Spec

import scalaz._

class TraversalSpec extends Spec {

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


  checkAll("apply2 Traversal", TraversalLaws(coordinates))
  checkAll("fromTraverse Traversal" , TraversalLaws(all[Int]))

  checkAll("traversal.asSetter", SetterLaws(coordinates.asSetter))

  // test implicit resolution of type classes

  "Traversal has a Compose instance" in {
    Compose[Traversal].compose(coordinates, all[Location])
      .modify(_ + 1)(IList(Location(1,2,""), Location(3,4,""))) ==== IList(Location(2,3,""), Location(4,5,""))
  }

  "Traversal has a Category instance" in {
    Category[Traversal].id[Int].getAll(3) ==== IList(3)
  }

  "Traversal has a Choice instance" in {
    Choice[Traversal].choice(all[Int], coordinates).modify(_ + 1)(-\/(IList(1,2,3))) ==== -\/(IList(2,3,4))
  }

}
