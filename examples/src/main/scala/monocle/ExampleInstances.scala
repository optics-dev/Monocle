package monocle

import monocle.Macro._

object ExampleInstances {

  case class Location(_latitude: Double, _longitude: Double)
  case class Address(_city: String, _postcode: String, _location: Location)
  case class Person(_age: Int, _name: String, _address: Address)

  object Location {
    val locationTraversal = Traversal.make2[Location, Location, Double, Double](_._latitude)(_._longitude){ case (from, newLat, newLong) =>
      from.copy(_latitude = newLat, _longitude = newLong)
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
