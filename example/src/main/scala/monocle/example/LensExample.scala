package monocle.example

import monocle.Lens

object LensExample {

  case class Person(name: String, age: Int, address: Address)
  case class Address(streetNumber: Int, streetName: String)

  val john = Person("John", 20, Address(10, "High Street"))

  val _age    = Lens[Person, Int](_.age)(a => p => p.copy(age = a))
  val _address = Lens[Person, Address](_.address)(a => p => p.copy(address = a))

  val _streetName   = Lens[Address, String](_.streetName)(s => a => a.copy(streetName = s))

}
