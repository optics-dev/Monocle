![Monocle Logo](https://raw.github.com/julien-truffaut/Monocle/master/image/logo.png)<br>
## Build
[![Build Status](https://api.travis-ci.org/julien-truffaut/Monocle.png?branch=master)](https://travis-ci.org/julien-truffaut/Monocle)

```scala
import sbt._
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

val scalaVersion   = "2.11.1" // or "2.10.4"
val libraryVersion = "0.4.0"  // or "0.5-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"    % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-generic" % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-macro"   % libraryVersion,         // since 0.4.0
  "com.github.julien-truffaut"  %%  "monocle-law"     % libraryVersion % "test" // since 0.4.0
)
```
## Motivation

Monocle is a `Lens` library, or more generally an Optics library where Optics gather the concepts
of `Lens`, `Traversal`, `Optional`, `Prism` and `Iso`. Monocle is strongly inspired by Haskell [Lens](https://github.com/ekmett/lens).

#### What does it mean?

Optics are a set of purely functional abstractions to manipulate (get, set, modify) immutable objects.
Optics compose between each other and particularly shine with nested objects.

#### Why do I need this?

Scala already provides getters and setters for case classes but modifying nested object is verbose which makes code
difficult to understand and reason about. Let's have a look at some examples:

```scala
case class Street(name: String, ...)     // ... means it contains other fields
case class Address(street: Street, ...)
case class Company(address: Address, ...)
case class Employee(company: Company, ...)
```

Let's say we have an employee and we need to set the first character of his company street name address in upper case.
Here is how we could write it in vanilla Scala:

```scala
val employee: Employee = ...

employee.copy(
  company = employee.company.copy(
    address = employee.company.address.copy(
      name = employee.company.address.name.capitalize // luckily capitalize exists
    )
  )
)
```

As you can see copy is not convenient to update nested objects as we need to repeat at each level the full path
to reach it. Let's see what could we do with Monocle:

```scala
val _name   : SimpleLens[Street  , String]  = ...  // we'll see later how to build Lens
val _street : SimpleLens[Address , Street]  = ...
val _address: SimpleLens[Company , Address] = ...
val _company: SimpleLens[Employee, Company] = ...

import monocle.syntax._

employee applyLens   _company
         composeLens _address
         composeLens _street
         composeLens _name
         modify (_.capitalize)
```

or with some syntax sugar:

```scala
employee |-> _company |-> _address |-> _street |-> _name modify (_.capitalize)
```

ComposeLens takes two `Lens`, one from A to B and another from B to C and creates a third `Lens` from A to C.
Therefore, after composing _company, _address, _street and _name, we obtain a `Lens` from `Employee` to `String` (the street name).

#### More abstractions

In the above example, we used capitalize to upper case the first letter of a `String`.
It works but it would be clearer if we could use `Lens` to zoom into the first character of a `String`.
However, we cannot write such a `Lens` because a `Lens` defines how to focus from an object `S` into a *mandatory*
object `A` and in our case, the first character of a `String` is optional as a `String` might be empty. For this
 we need a sort of partial `Lens`, in Monocle it is called `Optional`.

```scala
import monocle.syntax._
import monocle.function.HeadOption._ // to use headOption

employee applyLens   _company
         composeLens _address
         composeLens _street
         composeLens _name
         composeOptional headOption // generic Optional that focus into the first element
         modify toUpper
```

or with some syntax sugar:

```scala
employee |-> _company |-> _address |-> _street |-> _name |-? headOption modify toUpper
```

Similarly to composeLens, composeOptional takes two `Optional`, one from A to B and another from B to C and
creates a third `Optional` from A to C. All `Lens` can be seen as `Optional` where the optional element to zoom to is always
present, hence composing an `Optional` and a `Lens` always produces an `Optional` (see class diagram for full inheritance
relation between Optics).

For more examples, see the [```example``` module](example/src/test/scala/monocle).

## Lens Creation

`Lens` can be created by a pair of getter and setter:

```scala
val _company = SimpleLens[Employee](_.company)((e, c) => e.copy(company = c))
```

This is quite a lot of boiler plate, so Monocle provides a macro to simplify `Lens` creation:

```scala
import monocle.Macro._ // require monocle-macro dependency

val _company = mkLens[Employee, Company]("company") // company is checked at compiled time to be a valid accessor
```

In future version of the library, we are planning to introduce helpers to facilitate even further `Lens` creation.

## Polymorphic Optics and Instance Location Policy

A polymorphic optic is an optic that is applicable to different types. For example, `headOption` is an `Optional` from
some type `S` to its optional first element of type `A`. In order to use `headOption` (or any polymorphic optics), you
need to:

1.   import the polymorphic optic in your scope via `import monocle.function.headoption._` or `import monocle.function._`
2.   have the required instance of the type class `HeadOption` in your scope, e.g. if you want to use `headOption` from
     a `List[Int]`, you need an instance of `HeadOption[List[Int], Int]`. This instance can be either provided
     by you or by Monocle.

Monocle defines polymorphic optic instances in the following packages:

1.   `monocle.std` for standard Scala library classes, e.g. `List, Vector, Map`
2.   `monocle.scalaz` for Scalaz classes, e.g. `IList, OneAnd, Tree`
3.   `monocle.generic` for Shapeless classes, e.g. `HList, CoProduct`

An [example](example/src/test/scala/other/ImportExample) shows how to use Monocle imports.

## Overview
![Class Diagram](https://raw.github.com/julien-truffaut/Monocle/master/image/class-diagram.png)<br>
#### Sub Projects
Core contains the main library concepts: Lens, Traversal, Prism, Iso, Getter and Setter.
Core only depends on [scalaz](https://github.com/scalaz/scalaz) for type classes.

Law defines Iso, Lens, Prism, Setter and Traversal laws using [scalacheck](http://www.scalacheck.org/).

Macro defines a macro to reduce Lens creation boiler plate.

Generic is an experiment to provide highly generalised Lens and Iso using HList from [shapeless](https://github.com/milessabin/shapeless).
Generic focus is on neat abstraction but that may come at additional runtime or compile time cost.

Example shows how other sub projects can be used.
#### Contributor Handbook
We are happy to have as many people as possible contributing to Monocle.
Therefore, we made this small workflow to simplify the process:

1.   Select or create an issue (issues tagged with label "padawan-friendly" are designed for Scala novice)
2.   Comment on the issue letting everyone knows that you are working on it.
3.   Fork Monocle
4.   Work on your fork until you are satisfied (label your commits with issue number)
5.   Submit a [pull request](https://help.github.com/articles/using-pull-requests)
6.   We will review your pull request and merge it back to master

If you have any questions, we have irc channel on [freenode](http://webchat.freenode.net/) #scala-monocle and a [mailing group](https://groups.google.com/forum/#!forum/scala-monocle)

Thank you for you contribution!
### Contributors
Julien Truffaut - [@JulienTruffaut](https://twitter.com/JulienTruffaut "@JulienTruffaut") </a><br>
Ross Huggett - ross.huggett@gmail.com / [@rosshuggett](http://twitter.com/rosshuggett "@rosshuggett") </a><br>
Ilan Godik - ilan3580@gmail.com / [NightRa](https://github.com/NightRa "NightRa") </a><br>
