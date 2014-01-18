Lens
====

Lens library in scala


case class Person(name: String, age: Int, address: Address)
case class Address(city: String, postcode: String)

object AddressLens extends Lens[Person, Address]
object CityLens    extends Lens[Address, String]

val address = Address("London", "EC1...")
val person  = Person("Roger", 25, address)


Basic scala syntax to update a nested record:

person.copy(address = person.address.copy(city = "Paris"))

Lens version:

(AddressLens >- CityLens).set(person, "Paris")
