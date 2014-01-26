import lens.impl.HTraversal
import lens.Macro._
import scalaz.Applicative

package object lens {

  case class Location(_latitude: Double, _longitude: Double)
  case class Address(_city: String, _postcode: String, _location: Location)
  case class Person(_age: Int, _name: String, _address: Address)

  object Location {
    val locationTraversal = new HTraversal[Location, Double] {
      protected def traversalFunction[F[_] : Applicative](lift: Double => F[Double], from: Location): F[Location] = {
        import scalaz.syntax.applicative._
        (lift(from._latitude) |@| lift(from._longitude))((newLatitude, newLongitude) =>
          from.copy(_latitude = newLatitude, _longitude = newLongitude)
        )
      }
    }

    val latitude  = mkLens[Location, Double]("_latitude")
    val longitude = mkLens[Location, Double]("_longitude")
  }

  object Address {
    val postcode = mkLens[Address, String]("_postcode")
    val city     = mkLens[Address, String]("_city")
    val location = mkLens[Address, Location]("_location")
  }

  object Person {
    val age     = mkLens[Person, Int]("_age")
    val name    = mkLens[Person, String]("_name")
    val address = mkLens[Person, Address]("_address")
  }

}
