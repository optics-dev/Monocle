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

## Update a single element inside a List/Vector

In this example, `User` contains a `List` of `DebitCard`. Let's imagine we want to update the expiration date of
the first debit card. 

```scala mdoc:reset:silent
import java.time.YearMonth

case class User(name: String, debitCards: List[DebitCard])
case class DebitCard(cardNumber: String, expirationDate: YearMonth, securityCode: Int)

val anna = User(
  "Anna",
  List(
    DebitCard("4568 5794 3109 3087", YearMonth.of(2022, 4), 361),
    DebitCard("5566 2337 3022 2470", YearMonth.of(2024, 8), 990)
  )
)

val bob = User("Bob", List())
```

In Scala 3
```scala
import monocle.Focus
import monocle.syntax.all._

anna
  .focus(_.debitCards)  // soon .focus(_.debitCards.index(0).expirationDate)
  .index(0)
  .andThen(Focus[DebitCard](_.expirationDate))
  .replace(YearMonth.of(2026, 2))
// res: User = User(
//   name = "Anna",
//   debitCards = List(
//     DebitCard(
//       cardNumber = "4568 5794 3109 3087",
//       expirationDate = 2026-02,
//       securityCode = 361
//     ),
//     DebitCard(
//       cardNumber = "5566 2337 3022 2470",
//       expirationDate = 2024-08,
//       securityCode = 990
//     )
//   )
// )

bob
  .focus(_.debitCards) 
  .index(0)
  .andThen(Focus[DebitCard](_.expirationDate))
  .replace(YearMonth.of(2026, 2))
// res: User = User("Bob", List())
```

In Scala 2
```scala mdoc
import monocle.Focus
import monocle.macros.syntax.all._

anna
  .focus(_.debitCards) 
  .index(0)
  .andThen(Focus[DebitCard](_.expirationDate))
  .replace(YearMonth.of(2026, 2))

bob
  .focus(_.debitCards) 
  .index(0)
  .andThen(Focus[DebitCard](_.expirationDate))
  .replace(YearMonth.of(2026, 2))
```