package lens

import lens.impl.HTraversal
import lens.util.Identity
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.Matchers._
import org.scalatest.PropSpec
import org.scalatest.prop.PropertyChecks
import scala.language.higherKinds
import scalaz.Applicative

class TraversalSpec extends PropSpec with PropertyChecks {

  case class Location(latitude: Int, longitude: Int)

  object LatLongTraversal extends HTraversal[Location, Int] {
    protected def traversalFunction[F[_] : Applicative](lift: Int => F[Int], from: Location): F[Location] =
      Applicative[F].apply2(lift(from.latitude), lift(from.longitude)){ case (newLatitude, newLongitude) =>
        from.copy(latitude = newLatitude, longitude = newLongitude)
      }
  }

  implicit val locationGen : Arbitrary[Location] = Arbitrary(for {
    x <- arbitrary[Int]
    y <- arbitrary[Int]
  } yield Location(x, y))


  property("set - get") {
    forAll { (location: Location, n: Int) =>
      LatLongTraversal.get(LatLongTraversal.set(location, n)) should be (List(n,n))
    }
  }

  property("set - set") {
    forAll { (location: Location, n: Int) =>
      LatLongTraversal.set(location, n) should be (LatLongTraversal.set(LatLongTraversal.set(location, n), n))
    }
  }

  property("modify - id") {
    forAll { (location: Location) =>
      LatLongTraversal.modify(location, identity) should be (location)
    }
  }

  property("lift - id") {
    forAll { (location: Location) =>
      LatLongTraversal.lift(location, Identity[Int] ).value should be (location)
    }
  }

}
