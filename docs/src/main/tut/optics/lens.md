---
layout: docs
title:  "Lens"
section: "optics_menu"
source: "core/src/main/scala/monocle/PLens.scala"
scaladoc: "#monocle.Lens"
---
# Lens

A `Lens` is an optic used to zoom inside a `Product`, e.g. `case class`, `Tuple`, `HList` or even `Map`.

`Lenses` have two type parameters generally called `S` and `A`: `Lens[S, A]` where `S` represents the `Product` and `A` an element inside of `S`.

Let's take a simple case class with two fields:

```tut:silent
case class Address(streetNumber: Int, streetName: String)
```

We can create a `Lens[Address, Int]` which zoom from an `Address` to its field `streetNumber` by supplying a pair of functions:

*   `get: Address => Int`
*   `set: Int => Address => Address`

```tut:silent:invisible
// REPL bug: `error: not found: value n` if I rename _streetNumber to streetNumber
import monocle.Lens
val _streetNumber = Lens[Address, Int](_.streetNumber)(n => a => a.copy(streetNumber = n))
val streetNumber = _streetNumber
```

```scala
import monocle.Lens
val streetNumber = Lens[Address, Int](_.streetNumber)(n => a => a.copy(streetNumber = n))
```

This case is really straightforward so we automated the generation of `Lenses` from case classes using a macro:

```tut:silent
import monocle.macros.GenLens
val streetNumber = GenLens[Address](_.streetNumber)
```

Once we have a `Lens`, we can use the supplied `get` and `set` functions (nothing fancy!):

```tut:book
val address = Address(10, "High Street")

streetNumber.get(address)
streetNumber.set(5)(address)
```

We can also `modify` the target of `Lens` with a function, this equivalent to call `get` and then `set`:

```tut:book
streetNumber.modify(_ + 1)(address)

val n = streetNumber.get(address)
streetNumber.set(n + 1)(address)
```

We can push push the idea even further, with `modifyF` we can update the target of a `Lens` in a context, cf `scalaz.Functor`:

```tut:silent
def neighbors(n: Int): List[Int] =
  if(n > 0) List(n - 1, n + 1) else List(n + 1)

import scalaz.std.list._ // to get Functor[List] instance
```

```tut
streetNumber.modifyF(neighbors)(address)
streetNumber.modifyF(neighbors)(Address(135, "High Street"))
```

This would work with any kind of `Functor` and is especially useful in conjunction with asynchronous APIs, 
where one has the task to update a deeply nested structure with the result of an asynchronous computation:

```tut:silent
import scalaz.std.scalaFuture._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits._ // to get global ExecutionContext

def updateNumber(n: Int): Future[Int] = Future.successful(n + 1)
```

```tut:book
streetNumber.modifyF(updateNumber)(address)
```

Most importantly, `Lenses` compose together allowing to zoom deeper in a data structure

```tut:silent
case class Person(name: String, age: Int, address: Address)
val john = Person("John", 20, address)

val address = GenLens[Person](_.address)
```

```tut:book
(address composeLens streetNumber).get(john)
(address composeLens streetNumber).set(2)(john)
```

## Lens Generation

`Lens` creation is rather boiler platy but we developed a few macros to generate them automatically. All macros
are defined in a separate module:

```scala
libraryDependencies += "com.github.julien-truffaut"  %%  "monocle-macro"  % ${version}
```

```tut:silent
import monocle.macros.GenLens
val age = GenLens[Person](_.age)
```

`GenLens` can also be used to generate `Lens` several level deep:

```tut
GenLens[Person](_.address.streetName).set("Iffley Road")(john)
```

For those who want to push `Lenses` generation even further, we created `@Lenses` macro annotation which generate
`Lenses` for *all* fields of a case class. The generated `Lenses` are in the companion object of the case class:

```tut:silent
import monocle.macros.Lenses
@Lenses case class Point(x: Int, y: Int)
val p = Point(5, 3)
```

```tut:book
Point.x.get(p)
Point.y.set(0)(p)
```

You can also add a prefix to `@Lenses` in order to prefix the generated `Lenses`: 

```tut:silent
@Lenses("_") case class Point(x: Int, y: Int)
val p = Point(5, 3)
```

```tut:book
Point._x.get(p)
```

## Laws

A `Lens` must satisfies all properties defined in `LensLaws` from the `core` module.
You can check the validity of your own `Lenses` using `LensTests` from the `law` module.

In particular, a `Lens` must respect the `getSet` law which states that if you `get` a value `A` from `S` and 
`set` it back in, the result is an object identical to the original one. A side effect of this law is that `set` 
must only update the `A` it points to, for example it cannot increment a counter or modify another value.

```tut:silent
def getSet[S, A](l: Lens[S, A], s: S): Boolean =
  l.set(l.get(s))(s) == s
```

On the other hand, the `setGet` law states that if you `set` a value, you always `get` the same value back. 
This law guarantees that `set` is actually updating a value `A` inside of `S`.

```tut:silent
def setGet[S, A](l: Lens[S, A], s: S, a: A): Boolean =
  l.get(l.set(a)(s)) == a
```