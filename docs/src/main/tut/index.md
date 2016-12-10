---
layout: home
title:  "Home"
section: "home"
---

[![Join the chat at https://gitter.im/julien-truffaut/Monocle](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/julien-truffaut/Monocle?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.julien-truffaut/monocle_2.11.svg)](http://search.maven.org/#search|ga|1|com.github.julien-truffaut.monocle)
[![Build Status](https://api.travis-ci.org/julien-truffaut/Monocle.svg?branch=master)](https://travis-ci.org/julien-truffaut/Monocle)

## Table of contents
- [Motivation](#motivation)
- [Lens creation](#lens-creation)
- [Typeclass and instance location](#typeclass-and-instance-location)
- [Optics hierarchy](#optics-hierarchy)
- [Maintainers and contributors](#maintainers-and-contributors)
- [Copyright and licence](#copyright-and-license)

### Motivation

Monocle is a `Lens` library, or more generally an Optics library where Optics gather the concepts
of `Lens`, `Traversal`, `Optional`, `Prism` and `Iso`. Monocle is strongly inspired by Haskell [Lens](https://github.com/ekmett/lens).

#### What does it mean?

Optics are a set of purely functional abstractions to manipulate (get, set, modify) immutable objects.
Optics compose between each other and particularly shine with nested objects.

#### Why do I need this?

Scala already provides getters and setters for case classes but modifying nested object is verbose which makes code
difficult to understand and reason about. Let's have a look at some examples:

```tut:silent
case class Street(number: Int, name: String)
case class Address(city: String, street: Street)
case class Company(name: String, address: Address)
case class Employee(name: String, company: Company)
```

Let's say we have an employee and we need to set the first character of his company street name address in upper case.
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

As you can see copy is not convenient to update nested objects as we need to repeat at each level the full path
to reach it. Let's see what could we do with Monocle (type annotations are only for clarity):

```tut:silent
import monocle.Lens
import monocle.macros.GenLens

val company   : Lens[Employee, Company] = GenLens[Employee](_.company)
val address   : Lens[Company , Address] = GenLens[Company](_.address)
val street    : Lens[Address , Street]  = GenLens[Address](_.street)
val streetName: Lens[Street  , String]  = GenLens[Street](_.name)
```

```tut:book
(company composeLens address composeLens street composeLens streetName).modify(_.capitalize)(employee)
```

ComposeLens takes two `Lens`, one from A to B and another from B to C and creates a third `Lens` from A to C.
Therefore, after composing `company`, `address`, `street` and `name`, we obtain a `Lens` from `Employee` to `String` (the street name).

#### More abstractions

In the above example, we used capitalize to upper case the first letter of a `String`.
It works but it would be clearer if we could use `Lens` to zoom into the first character of a `String`.
However, we cannot write such a `Lens` because a `Lens` defines how to focus from an object `S` into a *mandatory*
object `A` and in our case, the first character of a `String` is optional as a `String` might be empty. For this
we need a sort of partial `Lens`, in Monocle it is called `Optional`.

```tut:silent
import monocle.function.Cons.headOption // to use headOption (an optic from Cons typeclass)
```

```tut:book
(company composeLens address
         composeLens street
         composeLens streetName
         composeOptional headOption).modify(_.toUpper)(employee)
```

Similarly to composeLens, composeOptional takes two `Optional`, one from A to B and another from B to C and
creates a third `Optional` from A to C. All `Lens` can be seen as `Optional` where the optional element to zoom to is always
present, hence composing an `Optional` and a `Lens` always produces an `Optional` (see class diagram for full inheritance
relation between Optics).

### Lens creation

There are 3 ways to create `Lens`, each with their pro and cons:

1.   The manual method where we construct a `Lens` by passing `get` and `set` functions:

     ```scala
     import monocle.Lens
     val company = Lens[Employee, Company](_.company)(c => e => e.copy(company = c))
     // or with some type inference
     val company = Lens((_: Employee).company)(c => e => e.copy(company = c))
     ```

2.   The semi-automatic method using the `GenLens` blackbox macro:

     ```scala
     import monocle.macros.GenLens
     val company = GenLens[Employee](_.company)
     ```

3.   Finally, the fully automatic method using the `@Lenses` macro annotation.
     `@Lenses` generates `Lens` for every accessor of a case class in its companion object (even if there is no companion object defined).
     This solution is the most boiler plate free but it has several disadvantages:
     1.   users need to add the macro paradise plugin to their project.
     2.   requires access to the case classes since you need to annotate them.

     ```scala
     import monocle.macros.Lenses
     @Lenses case class Employee(name: String, company: Company)
     // generates Employee.company: Lens[Employee, Company]
     // and       Employee.name   : Lens[Employee, String]

     // you can add a prefix to Lenses constructor

     @Lenses("_")
     case class Employee(company: Company, name: String)

     // generates Employee._company: Lens[Employee, Company]
     ```

Note: `GenLens` and `@Lenses` are both limited to case classes

### Optics in the REPL and tut

`Iso`, `Prism`, `Lens`, `Optional`, `Traversal` and `Setter` are all type aliases for more general polymorphic optics,
for example here is the definition of `Lens`:

```scala
type Lens[S, A] = PLens[S, S, A, A]

object Lens {
  def apply[S, A](get: S => A)(set: A => S => S): Lens[S, A] =
    PLens(get)(set)
}
```

This is a completely fine Scala definition and it will work perfectly in your code. However, if you try to create optics
in the REPL you will probably encounter a similar error:

```
scala> import monocle.Lens
import monocle.Lens

scala> case class Example(s: String, i: Int)
defined class Example

scala> val s = Lens[Example, String](_.s)(s => _.copy(s = s))
s: monocle.Lens[Example,String] = monocle.PLens$$anon$7@46aa4219

scala> val i = Lens[Example, Int](_.i)(i => _.copy(i = i))
<console>:13: error: object Lens does not take type parameters.
       val i = Lens[Example, Int](_.i)(i => _.copy(i = i))
```

We managed to create the first `Lens` but the second call to `apply` failed. This is a known bug in the REPL which is
tracked by [SI-7139](https://issues.scala-lang.org/browse/SI-7139). You will also face this error if you use [tut](https://github.com/tpolecat/tut)
to create documentation. This issue should be fixed in scala 2.12.1

### Typeclass and instance location

All typeclasses are defined in `monocle.function` package, you can import optic individually with

```scala
import monocle.function.$TYPE_CLASS.$OPTIC
```

For example

```tut:silent
import monocle.function.At.at
import monocle.function.Cons.{headOption, tailOption}
```

or you can import all typeclass based optics with

```tut:silent
import monocle.function.all._
```

Here is a complete example

```tut:reset:silent
import monocle.function.all._
import monocle.macros.GenLens

case class Foo(s: String, is: List[Int])
val foo = Foo("Hello", List(1,2,3))

val is = GenLens[Foo](_.is)
```

```tut:book
(is composeOptional headOption).getOption(foo)
```

Note: if you use a version of monocle before 1.4.x, you need another import to get the typeclass instance

```tut:silent
import monocle.std.list._
```

### Optics hierarchy
![Class Diagram](https://raw.github.com/julien-truffaut/Monocle/master/image/class-diagram.png)<br>

### Maintainers and contributors
The current maintainers (people who can merge pull requests) are:

* Julien Truffaut - [@julien-truffaut](https://github.com/julien-truffaut)
* Ilan Godik - [@NightRa](https://github.com/NightRa)
* Naoki Aoyama - [@aoiroaoino](https://github.com/aoiroaoino)
* Kenji Yoshida - [@xuwei-k](https://github.com/xuwei-k)

and the [contributors](https://github.com/julien-truffaut/Monocle/graphs/contributors) (people who committed to Monocle).

### Copyright and license

All code is available to you under the MIT license, available [here](http://opensource.org/licenses/mit-license.php).
The design is informed by many other projects, in particular Haskell [Lens](https://github.com/ekmett/lens).

Copyright the maintainers, 2016.
