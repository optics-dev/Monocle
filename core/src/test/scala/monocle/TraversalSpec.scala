package monocle

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.specs2.scalaz.Spec
import scalaz.std.AllInstances._
import scalaz.Equal

class TraversalSpec extends Spec {

  case class Location(latitude: Int, longitude: Int, name: String)

  val locationTraversal = Traversal.apply2[Location, Location, Int, Int](_.latitude)(_.longitude) {
    case (from, newLat, newLong) =>
      from.copy(latitude = newLat, longitude = newLong)
  }

  implicit val locationGen: Arbitrary[Location] = Arbitrary(for {
    x <- arbitrary[Int]
    y <- arbitrary[Int]
    n <- arbitrary[String]
  } yield Location(x, y, n))

  implicit val exampleEq = Equal.equalA[Location]

  checkAll(Traversal.laws(locationTraversal))

  case class Contact(name: String, phoneNumbers: List[String])

  val contactTraversal = Traversal.apply[List, Contact, Contact, String, String](
    _.phoneNumbers, (p, ns) => p.copy(phoneNumbers = ns))

  implicit val contactGen: Arbitrary[Contact] = Arbitrary(for {
    n <- arbitrary[String]
    ns <- arbitrary[List[String]]
  } yield Contact(n, ns))

  implicit val contactEq = Equal.equalA[Contact]

  checkAll("getAll/setAll", Traversal.laws(contactTraversal))
}
