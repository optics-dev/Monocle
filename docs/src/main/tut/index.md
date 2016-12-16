---
layout: home
title:  "Home"
section: "home"
---

[![Join the chat at https://gitter.im/julien-truffaut/Monocle](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/julien-truffaut/Monocle?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.julien-truffaut/monocle_2.12.svg)](http://search.maven.org/#search|ga|1|com.github.julien-truffaut.monocle)
[![Build Status](https://api.travis-ci.org/julien-truffaut/Monocle.svg?branch=master)](https://travis-ci.org/julien-truffaut/Monocle)

Monocle is an optics library for Scala (and [Scala.js](https://www.scala-js.org/)) strongly inspired by Haskell [Lens](https://github.com/ekmett/lens).

Optics are a group of purely functional abstractions to manipulate (`get`, `set`, `modify`, ...) immutable objects.

## Getting started

Monocle is published to Maven Central and cross-built for Scala `2.10`, `2.11`, and `2.12` so you can just add the following to your build:

```scala
val monocleVersion = "1.4.0-M1"

libraryDependencies ++= Seq(
  "com.github.julien-truffaut" %%  "monocle-core"  % monocleVersion,
  "com.github.julien-truffaut" %%  "monocle-macro" % monocleVersion,
  "com.github.julien-truffaut" %%  "monocle-law"   % monocleVersion % "test"
)
```

If you want to use macro annotations such as `@Lenses`, you will also need to include:

```scala
addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
```

## Motivation

Scala already provides getters and setters for case classes but modifying nested object is verbose which makes code
difficult to understand and reason about. Let's have a look at some examples:

```tut:silent
case class Street(number: Int, name: String)
case class Address(city: String, street: Street)
case class Company(name: String, address: Address)
case class Employee(name: String, company: Company)
```

Let's say we have an employee and we need to upper case the first character of his company street name.
Here is how we could write it in vanilla Scala:

```tut:silent
val employee = Employee("john", Company("awesome inc", Address("london", Street(23, "high street"))))
```

```tut:book
employee.copy(
  company = employee.company.copy(
    address = employee.company.address.copy(
      street = employee.company.address.street.copy(
        name = employee.company.address.street.name.capitalize // luckily capitalize exists
      )
    )
  )
)
```

As we can see `copy` is not convenient to update nested objects because we need to repeat ourselves.
Let's see what could we do with Monocle (type annotations are only added for clarity):

```tut:silent
import monocle.Lens
import monocle.macros.GenLens

val company   : Lens[Employee, Company] = GenLens[Employee](_.company)
val address   : Lens[Company , Address] = GenLens[Company](_.address)
val street    : Lens[Address , Street]  = GenLens[Address](_.street)
val streetName: Lens[Street  , String]  = GenLens[Street](_.name)

company composeLens address composeLens street composeLens streetName
```

`composeLens` takes two `Lenses`, one from `A` to `B` and another one from `B` to `C` and creates a third `Lens` from `A` to `C`.
Therefore, after composing `company`, `address`, `street` and `name`, we obtain a `Lens` from `Employee` to `String` (the street name).
Now we can use this `Lens` issued from the composition to `modify` the street name using the function `capitalize`:

```tut:book
(company composeLens address composeLens street composeLens streetName).modify(_.capitalize)(employee)
```

Here `modify` lift a function `String => String` to a function `Employee => Employee`.
It works but it would be clearer if we could zoom into the first character of a `String` with a `Lens`.
However, we cannot write such a `Lens` because `Lenses` require the field they are directed at to be *mandatory*.
In our case the first character of a `String` is optional as a `String` can be empty.
So we need another abstraction that would be a sort of partial `Lens`, in Monocle it is called an `Optional`.

```tut:silent
import monocle.function.Cons.headOption // to use headOption (an optic from Cons typeclass)
```

```tut:book
(company composeLens address
         composeLens street
         composeLens streetName
         composeOptional headOption).modify(_.toUpper)(employee)
```

Similarly to `composeLens`, `composeOptional` takes two `Optionals`, one from `A` to `B` and another from `B` to `C` and
creates a third `Optional` from `A` to `C`. All `Lenses` can be seen as `Optionals` where the optional element to zoom into is always
present, hence composing an `Optional` and a `Lens` always produces an `Optional` (see class [diagram](optics.html) for full inheritance
relation between optics).

Monocle offers various functions and macros to cut the boiler plate even further, here is an example:

```tut:book
import monocle.macros.syntax.lens._

employee.lens(_.company.address.street.name).composeOptional(headOption).modify(_.toUpper)
```

Please consult the [documentation](modules.html) or the [scaladoc](/Monocle/api) for more details and examples.

## Maintainers and contributors

The current maintainers (people who can merge pull requests) are:

* Julien Truffaut - [@julien-truffaut](https://github.com/julien-truffaut)
* Ilan Godik - [@NightRa](https://github.com/NightRa)
* Naoki Aoyama - [@aoiroaoino](https://github.com/aoiroaoino)
* Kenji Yoshida - [@xuwei-k](https://github.com/xuwei-k)

and the [contributors](https://github.com/julien-truffaut/Monocle/graphs/contributors) (people who committed to Monocle).

## Copyright and license

All code is available to you under the MIT license, available [here](http://opensource.org/licenses/mit-license.php).
The design is informed by many other projects, in particular Haskell [Lens](https://github.com/ekmett/lens).

Copyright the maintainers, 2016.
