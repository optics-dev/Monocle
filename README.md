![Monocle Logo](https://raw.github.com/julien-truffaut/Monocle/master/image/logo.png)<br>
## Build
[![Build Status](https://api.travis-ci.org/julien-truffaut/Monocle.png?branch=master)](https://travis-ci.org/julien-truffaut/Monocle)

```scala
import sbt._
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

val scalaVersion   = "2.11.2" // or "2.10.4"
val libraryVersion = "0.5.1"  // or "1.0.0-SNAPSHOT"

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

(_company composeLens _address composeLens _street composeLens _name).modify(employee, _.capitalize)

import monocle.syntax._ // to use optics as operator 

employee applyLens   _company
         composeLens _address
         composeLens _street
         composeLens _name
         modify (_.capitalize)
         
// or with some syntax sugar
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
import monocle.function.HeadOption._ // to use headOption (a polymorphic optic)
import monocle.std.string._          // to get String instance for HeadOption


employee applyLens   _company
         composeLens _address
         composeLens _street
         composeLens _name
         composeOptional headOption
         modify toUpper
         
// or with some syntax sugar         
employee |-> _company |-> _address |-> _street |-> _name |-? headOption modify toUpper
```

Similarly to composeLens, composeOptional takes two `Optional`, one from A to B and another from B to C and
creates a third `Optional` from A to C. All `Lens` can be seen as `Optional` where the optional element to zoom to is always
present, hence composing an `Optional` and a `Lens` always produces an `Optional` (see class diagram for full inheritance
relation between Optics).

For more examples, see the [```example``` module](example/src/test/scala/monocle).

## Lens Creation

There are four ways to create `SimpleLens`, each with their pro and cons:

1.   The first method is the most verbose but it fully documents the type of the `SimpleLens` created. 
     Remark, this is the only constructor that can also be used for `Lens` (i.e. with 4 type parameters): 
     
     ```scala
     val _company = SimpleLens[Employee, Company](_.company, (e, c) => e.copy(company = c))
     ```
     
2.   The next solution is slightly shorter as it does not require to define the second type parameter of `SimpleLens`
     (it is inferred from the type of the first argument):

     ```scala
     val _company = SimpleLens[Employee](_.company)((e, c) => e.copy(company = c))
     ```

3.   Now we start using the heavy artillery ... Macro. This method can only be used on case classes and since macros are
     experimental in scala, there is no guarantee that future version of Monocle will support it (even though we will do 
     our best):

     ```scala
      import monocle.Macro._ // require monocle-macro dependency

      val _company = mkLens[Employee, Company]("company") // company is checked at compiled time to be a valid accessor
      ```
      Note: this macro is deprecated in 0.5.1

4.   An alternative Macro syntax uses a dedicated object to capture the class, and a simple closure to define the field.
     This syntax is more IDE-friendly.

     ```scala
     val lenser = Lenser[Employee]
     
     val _company = lenser(_.company) 
     ```
     
     A `Lenser` can be in-lined or re-used to avoid specifying the class type parameter.

5.   Finally, the boiler plate free solution with macro annotation (which are probably the most experimental part of macros).
     Adding `@Lenses` annotation on case class will generate `SimpleLens` for every single accessor of the case class.
     These generated `SimpleLens` are in the companion object of the case class (even if there is no companion object declared).
     Nevertheless, this solution has several disadvantages: 
     1.   users need to add the macro paradise plugin to their project.
     2.   IDE have a poor support for Macro annotation, therefore it is likely your IDE will not know that the generated `SimpleLens`
          exist (but it will compile). If you want a better IDE support, please vote on the following [issue](http://youtrack.jetbrains.com/issue/SCL-7419). 
     3.   this solution can only be applied when you control the case classes since you need to annotate them. This means that
          you cannot use this technique for classes defined in another project.
     
     ```scala
     @Lenses
     case class Employee(company: Company, name: String, ...)
     
     // generates Employee.company: SimpleLens[Employee, Company]
     // and       Employee.name   : SimpleLens[Employee, String]
     ```

## Polymorphic Optics and Instance Location Policy

A polymorphic optic is an optic that is applicable to different types. For example, `headOption` is an `Optional` from
some type `S` to its optional first element of type `A`. In order to use `headOption` (or any polymorphic optics), you
need to:

1.   import the polymorphic optic in your scope via `import monocle.function.headoption._` or `import monocle.function._`
2.   have the required instance of the type class `HeadOption` in your scope, e.g. if you want to use `headOption` from
     a `List[Int]`, you need an instance of `HeadOption[List[Int], Int]`. This instance can be either provided
     by you or by Monocle.

Monocle defines polymorphic optic instances in the following packages:

1.   `monocle.std` for standard Scala library and Scalaz classes, e.g. `List, Vector, Map, IList, OneAnd`
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
Adam Warski - [@adamwarski](http://twitter.com/adamwarski "@adamwarski") </a><br>
Dale Wijnand - [@dwijnand](http://twitter.com/dwijnand "@dwijnand") </a><br>
