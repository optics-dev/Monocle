![Monocle Logo](https://raw.github.com/julien-truffaut/Monocle/master/image/logo.png)<br>
## Build

[![Join the chat at https://gitter.im/julien-truffaut/Monocle](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/julien-truffaut/Monocle?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://api.travis-ci.org/julien-truffaut/Monocle.png?branch=master)](https://travis-ci.org/julien-truffaut/Monocle)

```scala
import sbt._
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

val scalaVersion   = "2.11.6"  // or "2.10.4"
val libraryVersion = "1.1.1"   // or "1.2.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"    % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-generic" % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-macro"   % libraryVersion,        
  "com.github.julien-truffaut"  %%  "monocle-law"     % libraryVersion % "test" 
)

// for @Lenses macro support
addCompilerPlugin("org.scalamacros" %% "paradise" % "2.0.1" cross CrossVersion.full)
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
      street = employee.company.address.street.copy(
        name = employee.company.address.street.name.capitalize // luckily capitalize exists
      }
    )
  )
)
```

As you can see copy is not convenient to update nested objects as we need to repeat at each level the full path
to reach it. Let's see what could we do with Monocle:

```scala
val _name   : Lens[Street  , String]  = ...  // we'll see later how to build Lens
val _street : Lens[Address , Street]  = ...
val _address: Lens[Company , Address] = ...
val _company: Lens[Employee, Company] = ...

(_company composeLens _address composeLens _street composeLens _name).modify(_.capitalize)(employee)

// you can achieve the same result with less characters using symbolic syntax

(_company ^|-> _address ^|-> _street ^|-> _name).modify(_.capitalize)(employee)
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
import monocle.function.headOption._ // to use headOption (a generic optic)
import monocle.std.string._          // to get String instance for HeadOption


((_company composeLens _address
           composeLens _street
           composeLens _name
           composeOptional headOption).modify(toUpper)(employee)
```

Similarly to composeLens, composeOptional takes two `Optional`, one from A to B and another from B to C and
creates a third `Optional` from A to C. All `Lens` can be seen as `Optional` where the optional element to zoom to is always
present, hence composing an `Optional` and a `Lens` always produces an `Optional` (see class diagram for full inheritance
relation between Optics).

For more examples, see the [`example` module](example/src/test/scala/monocle).

## Lens Creation

There are 3 ways to create `Lens`, each with their pro and cons:

1.   The manual method where we construct a `Lens` by passing `get` and `set` functions:
     
     ```scala
     val _company = Lens[Employee, Company](_.company)( c => e => e.copy(company = c))
     // or with some type inference
     val _company = Lens((_: Employee).company)( c => e => e.copy(company = c))
     ```

2.   The semi-automatic method using the `GenLens` blackbox macro:

     ```scala
     val _company = GenLens[Employee](_.company)
     val _name    = GenLens[Employee](_.name)
     
     // or
     val genLens = GenLens[Employee]
     val (_company, _name) = (genLens(_.company) , genLens(_.name))
     ```

3.   Finally, the fully automatic method using the `@Lenses` macro annotation.
     `@Lenses` generates `Lens` for every accessor of a case class in its companion object (even if there is no companion object defined).
     This solution is the most boiler plate free but it has several disadvantages:
     1.   users need to add the macro paradise plugin to their project.
     2.   poor IDE supports, at the moment only IntelliJ recognises the generated `Lens`.
     3.   requires access to the case classes since you need to annotate them.
     
     ```scala
     @Lenses case class Employee(company: Company, name: String, ...)
     
     // generates Employee.company: Lens[Employee, Company]
     // and       Employee.name   : Lens[Employee, String]
     
     // you can add a prefix to Lenses constructor
     
     @Lenses("_")
     case class Employee(company: Company, name: String, ...)
     
     // generates Employee._company: Lens[Employee, Company]
     ```

Note: `GenLens` and `@Lenses` are both limited to case classes

## Generic Optics and Instance Location Policy

A generic optic is an optic that is applicable to different types. For example, `headOption` is an `Optional` from
some type `S` to its optional first element of type `A`. In order to use `headOption` (or any generic optics), you
need to:

1.   import the generic optic in your scope via `import monocle.function.headOption._` or `import monocle.function._`
2.   have the required instance of the type class `monocle.HeadOption` in your scope, e.g. if you want to use `headOption` from
     a `List[Int]`, you need an instance of `HeadOption[List[Int], Int]`. This instance can be either provided
     by you or by Monocle.

Monocle defines generic optic instances in the following packages:

1.   `monocle.std` for standard Scala library and Scalaz classes, e.g. `List, Vector, Map, IList, OneAnd`
3.   `monocle.generic` for Shapeless classes, e.g. `HList, CoProduct`

An [example](example/src/test/scala/other/ImportExample.scala) shows how to use Monocle imports.

## Overview
![Class Diagram](https://raw.github.com/julien-truffaut/Monocle/master/image/class-diagram.png)<br>
#### Sub Projects
* Core contains the main library concepts: `Lens`, `Traversal`, `Prism`, `Optional`, `Iso`, `Getter` and `Setter`.
Core only depends on [scalaz](https://github.com/scalaz/scalaz) for type classes.
* Law defines properties for Optics using [scalacheck](http://www.scalacheck.org/).
* Macro defines a a set of macros to reduce Optics boiler plate.
* Generic is an experiment to provide highly generalised Optics using `HList` and `CoProduct` from [shapeless](https://github.com/milessabin/shapeless). Generic focus is on neat abstraction but that may come at additional runtime or compile time cost.
* Example shows how other sub projects can be used.

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
Adam Warski - [@adamwarski](http://twitter.com/adamwarski "@adamwarski") </a><br>
Dale Wijnand - [@dwijnand](http://twitter.com/dwijnand "@dwijnand") </a><br>
