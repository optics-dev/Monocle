package example

import scalaz.Functor
import lens.Lens
import lens.impl.HaskLens
import scalaz.std.option._


case class Address(city: String, postcode: String)
case class Person(age: Int, address: Address)

object AddressLens extends Lens[Person, Address] {
  def get(from: Person): Address = from.address

  def lift[F[_] : Functor](from: Person, f: Address => F[Address]): F[Person] =
    implicitly[Functor[F]].map(f(from.address))(newValue => from.copy(address = newValue))

}

object CityLens extends Lens[Address, String] {
  def get(from: Address): String = from.city
  def lift[F[_] : Functor](from: Address, f: String => F[String]): F[Address] =
    implicitly[Functor[F]].map(f(from.city))(newValue => from.copy(city = newValue))
}

object AddressHLens extends HaskLens[Person, Address] {
  protected def lensFunction[F[_] : Functor](lift: Address => F[Address], person: Person): F[Person] = {
    implicitly[Functor[F]].map(lift(person.address))(newValue => person.copy(address = newValue))
  }
}

object CityHLens extends HaskLens[Address, String] {
  protected def lensFunction[F[_] : Functor](lift: String => F[String], address: Address): F[Address] = {
    implicitly[Functor[F]].map(lift(address.city))(newValue => address.copy(city = newValue))
  }
}

object Example extends App {

  val address = Address("London", "EC1...")
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

}


