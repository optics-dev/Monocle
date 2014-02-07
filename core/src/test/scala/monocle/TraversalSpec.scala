package monocle

import monocle.TestHelper._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.specs2.scalaz.Spec
import scalaz.std.AllInstances._

class TraversalSpec extends Spec {

  case class Location(latitude: Int, longitude: Int, name: String)

  val locationTraversal = SimpleTraversal.make2[Location, Int](_.latitude)(_.longitude){ case (from, newLat, newLong) =>
   from.copy(latitude = newLat, longitude = newLong)
  }

  implicit val locationGen : Arbitrary[Location] = Arbitrary(for {
    x <- arbitrary[Int]
    y <- arbitrary[Int]
    n <- arbitrary[String]
  } yield Location(x, y, n))

  implicit val exampleEq = defaultEqual[Location]

  checkAll(SimpleTraversal.laws(locationTraversal))


//  property("get ordered") {
//    forAll { (location: Location) =>
//      LatLongTraversal.get(location) should be (List(location.latitude, location.longitude))
//    }
//  }


}
