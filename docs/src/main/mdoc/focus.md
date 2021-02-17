---
id: focus
title: Focus
---

`Focus` is the best starting point into Monocle. `Focus` lets you define a path within an immutable object. 
Then, once you reach your desired target, you can just as easily get, replace or modify it. Let’s have a look at the 
most common use cases.

An important point before we start. `Focus` is a macro available for both Scala 2.13 and Scala 3. 
However, the macro API has completely changed between Scala 2 and 3, so for each example, we will first show the 
version for Scala 3 and then Scala 2 (often more verbose). 

## Update a field of a case class

```scala mdoc:silent
case class User(name: String, address: Address)
case class Address(streetNumber: Int, streetName: String)

val anna = User("Anna", Address(12, "high street"))
```

In Scala 3
```scala
import monocle.syntax.all._

anna
  .focus(_.name)
  .replace("Bob")
// res: User = User(
//   name = "Bob",
//   address = Address(streetNumber = 12, streetName = "high street")
// )

anna
  .focus(_.address.streetNumber)
  .modify(_ + 1)
// res: User = User(
//   name = "Anna",
//   address = Address(streetNumber = 13, streetName = "high street")
// )
```

The Scala 2 version is the same, except for the import
```scala mdoc
import monocle.macros.syntax.all._

anna
  .focus(_.name)
  .replace("Bob")

anna
  .focus(_.address.streetNumber)
  .modify(_ + 1)
```


## Update an optional field of a case class

This time a user may or may not have an `Address`. 

```scala mdoc:reset:silent
case class User(name: String, address: Option[Address])
case class Address(streetNumber: Int, streetName: String)

val anna = User("Anna", Some(Address(12, "high street")))
val bob  = User("bob" , None)
```

In Scala 3
```scala
import monocle.syntax.all._

anna
  .focus(_.address.some.streetNumber)
  .modify(_ + 1)
// res: User = User(
//   name = "Anna",
//   address = Some(value = Address(streetNumber = 13, streetName = "high street"))
// )


bob
  .focus(_.address.some.streetNumber)
  .modify(_ + 1)
// res: User = User(name = "bob", address = None)
```

As you can see, focusing on the street number has no effect on `bob` because this instance doesn't have an address.

In Scala 2
```scala mdoc
import monocle.Focus
import monocle.macros.syntax.all._

anna
  .focus(_.address).some
  .andThen(Focus[Address](_.streetNumber))
  .modify(_ + 1)

bob
  .focus(_.address).some
  .andThen(Focus[Address](_.streetNumber))
  .modify(_ + 1)
```