Lens
====

Attempt to port the amazing Haskell [Lens](https://github.com/ekmett/lens) library to Scala


    case class Person(name: String, age: Int, address: Address)
    case class Address(city: String, postcode: String, location: Location)
    case class Location(latitude: Long, longitude: Long)

    object AddressLens  extends Lens[Person, Address]
    object CityLens     extends Lens[Address, String]
    object LocationLens extends Lens[Address, Location]
    object LatitudeLens extends Lens[Location, Long]

    val address = Address("London", "EC1...", Location(12, 34))
    val person  = Person("Roger", 25, address)

Get
---

    // Scala syntax:
    person.address.city

    // Lens syntax:
    Person2CityLens.get(person, "Paris")


Set
---

    // Scala syntax:
    person.copy(address = person.address.copy(city = "Paris"))

    // Lens syntax:
    (AddressLens >- CityLens).set(person, "Paris")


Modify
------

    // Scala syntax:
    val location = person.address.location
    person.copy(address =
      person.address.copy(location =
        person.address.location = Location(location.latitude + 1, location.longitude)
      )
    )

    // Lens syntax:
    (AddressLens >- CityLens >- LocationLens >- LatitudeLens).modify(person, _ + 1)

Lift
----

    def neighbours(location: Location): List[Location]

    // Scala syntax:
    val location = person.address.location
    neighbours(location) map { neighbourLocation =>
      person.copy(address =
        person.address.copy(location = neighbourLocation)
      )
    }

    // Lens syntax:
    (AddressLens >- CityLens >- LocationLens).lift(person, neighbours)

Traversal
=========

A Traversal is a lens toward 0 or more values

    object LongLatTraversal extends Traversal[Location, Double]
    val location = Location(2.0, 6.0)

Get
---

    // Scala syntax:
    List(location.latitude, location.longitude)

    // Traversal syntax:
    LongLatTraversal.get(location)

Set
---

    // Scala syntax:
    location.copy(latitude = 4.0, longitude = 4.0)

    // Traversal syntax:
    LongLatTraversal.set(location, 4.0)


Modify
------

    // Scala syntax:
    location.copy(latitude = location.latitude + 2, longitude = location.longitude + 2)

    // Traversal syntax:
    LongLatTraversal.modify(location, _ + 2)


Fold
----

Fold is slightly trickier, we need a way to accumulate or fold all values that we retrieve
Therefore, we need the return type to possess a Monoid instance or to manually define the fold logic

    implicit object Addition extends Monoid[Double] {
      def append(f1: Double, f2: => Double): Double = f1 + f2
      def zero: Double = 0L
    }

    // Scala syntax:
    location.latitude + location.longitude

    // Traversal syntax using implicit Monoid:
    LongLatTraversal.fold(location)

    // Traversal syntax using explicit fold logic:
    LongLatTraversal.fold(location, 0.0)(_ + _)


Composition
===========

This is the bread and butter of Lens and Traversal, they compose!

    Lens[A,B]      >- Lens[B,C]      : Lens[A,C]
    Traversal[A,B] >- Traversal[B,C] : Traversal[A,C]

    Traversal[A,B] >- Lens[B,C]      : Traversal[A,C]
    Lens[A,B]      >- Traversal[B,C] : Traversal[A,C]
