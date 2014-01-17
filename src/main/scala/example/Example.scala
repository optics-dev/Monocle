package example

import lens.{Lens => BasicLens }
import haskell.{Lens => HLens}
import scalaz.Functor


case class Address(city: String, postcode: String)
case class Person(age: Int, address: Address)

object AddressLens extends BasicLens[Person, Address] {
  def get(from: Person): Address = from.address
  def modify(from: Person, f: Address => Address): Person = from.copy(address = f.apply(from.address))
}


object CityLens extends BasicLens[Address, String] {
  def get(from: Address): String = from.city
  def modify(from: Address, f: String => String): Address = from.copy(city = f.apply(from.city))
}

object Age extends HLens[Person, Int] {
  protected def lensFunction[F[_] : Functor](age2FAge: Int => F[Int], person: Person): F[Person] = {
    val fAge: F[Int] = age2FAge.apply(person.age)
    implicitly[Functor[F]].map(fAge)(newAge => person.copy(age = newAge))
  }
}


object Example extends App {

  val address = Address("London", "EC1...")
  val person = Person(25, address)

  //basic scala
  println(person.copy(address = person.address.copy(city = "Paris")))

  val Person2City= AddressLens >- CityLens

  println(Person2City.set(person, "Paris") )
  println(Person2City.get(person))
  println(Person2City.modify(person, _ + "!!!"))

  println(Age.set(person, 20))

}


