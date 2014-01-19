package example

import lens.Lens
import lens.impl.{HTraversal, HLens}
import scala.language.higherKinds
import scalaz.std.option._
import scalaz.std.list._
import scalaz.{Monoid, Applicative, Functor}

case class Location(latitude: Double, longitude: Double)
case class Address(city: String, postcode: String, location: Location)
case class Person(age: Int, address: Address)

object Example extends App {

  object AddressLens extends Lens[Person, Address] {
    def get(from: Person): Address = from.address
    def lift[F[_] : Functor](from: Person, f: Address => F[Address]): F[Person] =
      Functor[F].map(f(from.address))(newValue => from.copy(address = newValue))
  }

  object CityLens extends Lens[Address, String] {
    def get(from: Address): String = from.city
    def lift[F[_] : Functor](from: Address, f: String => F[String]): F[Address] =
      Functor[F].map(f(from.city))(newValue => from.copy(city = newValue))
  }

  val AddressHLens  = HLens[Person, Address](_.address, (a, b) => a.copy(address = b))
  val CityHLens     = HLens[Address, String](_.city, (a, b) => a.copy(city = b))
  val LocationHLens = HLens[Address, Location](_.location, (a, b) => a.copy(location = b))

  object LatLongTraversal extends HTraversal[Location, Double] {
    protected def traversalFunction[F[_] : Applicative](lift: Double => F[Double], from: Location): F[Location] =
      Applicative[F].apply2(lift(from.latitude), lift(from.longitude)){ case (newLatitude, newLongitude) =>
        from.copy(latitude = newLatitude, longitude = newLongitude)
      }
  }

  val address = Address("London", "EC1...", Location(4.0, 6.0))
  val person = Person(25, address)

  //basic scala
  println(person.copy(address = person.address.copy(city = "Paris")))

  val Person2CityLens= AddressLens >- CityLens

  println(Person2CityLens.set(person, "Paris") )
  println(Person2CityLens.get(person))
  println(Person2CityLens.modify(person, _ + "!!!"))
  println(Person2CityLens.lift(person, city => Option(city)))

  val Person2CityHLens = AddressHLens >- CityHLens

  println(Person2CityHLens.set(person, "Paris") )
  println(Person2CityHLens.get(person))
  println(Person2CityHLens.modify(person, _ + "!!!"))
  println(Person2CityHLens.lift(person, city => Option(city)))

  val location = Location(2, 6)

  implicit object Addition extends Monoid[Double] {
    def append(f1: Double, f2: => Double): Double = f1 + f2
    def zero: Double = 0L
  }

  println(LatLongTraversal.get(location))
  println(LatLongTraversal.fold(location))
  println(LatLongTraversal.modify(location, _ + 2))
  println(LatLongTraversal.lift(location, l => List(l+1, l, l-1)))

  // composition of lens and traversal
  println((AddressHLens >- LocationHLens >- LatLongTraversal).get(person))

}




