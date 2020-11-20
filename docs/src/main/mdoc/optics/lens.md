---
id: lens
title: Lens
---

A `Lens` is an optic used to zoom inside a `Product`, e.g. `case class`, `Tuple`, `HList` or even `Map`.

`Lenses` have two type parameters generally called `S` and `A`: `Lens[S, A]` where `S` represents the `Product` and `A` an element inside of `S`.

Let's take a simple case class with two fields:

```scala mdoc:silent
case class Address(streetNumber: Int, streetName: String)
```

We can create a `Lens[Address, Int]` which zooms from an `Address` to its field `streetNumber` by supplying a pair of functions:

*   `get: Address => Int`
*   `set: Int => Address => Address`

```scala mdoc:silent
import monocle.Lens
val streetNumber = Lens[Address, Int](_.streetNumber)(n => a => a.copy(streetNumber = n))
```

This case is really straightforward so we automated the generation of `Lenses` from case classes using a macro:

```scala mdoc:nest:silent
import monocle.macros.GenLens
val streetNumber = GenLens[Address](_.streetNumber)
```

Once we have a `Lens`, we can use the supplied `get` and `set` functions (nothing fancy!):

```scala mdoc
val address = Address(10, "High Street")

streetNumber.get(address)
streetNumber.set(5)(address)
```

We can also `modify` the target of `Lens` with a function, this is equivalent to call `get` and then `set`:

```scala mdoc
streetNumber.modify(_ + 1)(address)

val n = streetNumber.get(address)
streetNumber.set(n + 1)(address)
```

We can push the idea even further, with `modifyF` we can update the target of a `Lens` in a context, cf `cats.Functor`:

```scala mdoc:silent
def neighbors(n: Int): List[Int] =
  if(n > 0) List(n - 1, n + 1) else List(n + 1)

import cats.implicits._ // to get all Functor instance
```

```scala mdoc
streetNumber.modifyF(neighbors)(address)
streetNumber.modifyF(neighbors)(Address(135, "High Street"))
```

This would work with any kind of `Functor` and is especially useful in conjunction with asynchronous APIs, 
where one has the task to update a deeply nested structure with the result of an asynchronous computation:

```scala mdoc:silent
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits._ // to get global ExecutionContext

def updateNumber(n: Int): Future[Int] = Future.successful(n + 1)
```

```scala mdoc
streetNumber.modifyF(updateNumber)(address)
```

Most importantly, `Lenses` compose together allowing to zoom deeper in a data structure

```scala mdoc:nest:silent
case class Person(name: String, age: Int, address: Address)
val john = Person("John", 20, Address(10, "High Street"))

val address = GenLens[Person](_.address)
```

```scala mdoc
address.andThen(streetNumber).get(john)
address.andThen(streetNumber).set(2)(john)
```

## Other Ways of Lens Composition

Is possible to compose few `Lenses` together by using `compose`:

```scala mdoc:silent
GenLens[Person](_.name).set("Mike") compose GenLens[Person](_.age).modify(_ + 1)
```

Same but with the simplified macro based syntax:

```scala mdoc:silent
import monocle.macros.syntax.lens._

john.lens(_.name).set("Mike").lens(_.age).modify(_ + 1)
```

(All `Setter` like optics offer `set` and `modify` methods that returns an `EndoFunction` (i.e. `S => S`) which means that we can compose modification using basic function composition.)

Sometimes you need an easy way to update `Product` type inside
`Sum` type - for that case you can compose `Prism` with `Lens` by using `some`:

```scala mdoc
import monocle.std.option.some
import monocle.macros.GenLens

case class B(c: Int)
case class A(b: Option[B])

val c = GenLens[B](_.c)
val b = GenLens[A](_.b)

b.some.andThen(c).getOption(A(Some(B(1))))
```

For more detailed view of the various optics composition see [Optics](../optics.html)

## Lens Generation

`Lens` creation is rather boiler platy but we developed a few macros to generate them automatically. All macros
are defined in a separate module (see [modules](../modules.html)).

```scala mdoc:silent
import monocle.macros.GenLens
val age = GenLens[Person](_.age)
```

`GenLens` can also be used to generate `Lens` several level deep:

```scala mdoc
GenLens[Person](_.address.streetName).set("Iffley Road")(john)
```

For those who want to push `Lenses` generation even further, we created `@Lenses` macro annotation which generate
`Lenses` for *all* fields of a case class. The generated `Lenses` are in the companion object of the case class:

```scala mdoc:silent
import monocle.macros.Lenses
@Lenses case class Point(x: Int, y: Int)
val p = Point(5, 3)
```

```scala mdoc
Point.x.get(p)
Point.y.set(0)(p)
```

You can also add a prefix to `@Lenses` in order to prefix the generated `Lenses`: 

```scala mdoc:nest:silent
@Lenses("_") case class PrefixedPoint(x: Int, y: Int)
val p = PrefixedPoint(5, 3)
```

```scala mdoc
PrefixedPoint._x.get(p)
```

Note: before using `@Lenses` remember to activate macro annotations. See [Getting started](../../#getting-started) section for instructions.

## Laws

A `Lens` must satisfy all properties defined in `LensLaws` from the `core` module.
You can check the validity of your own `Lenses` using `LensTests` from the `law` module.

In particular, a `Lens` must respect the `getSet` law which states that if you `get` a value `A` from `S` and 
`set` it back in, the result is an object identical to the original one. A side effect of this law is that `set` 
must only update the `A` it points to, for example it cannot increment a counter or modify another value.

```scala mdoc:silent
def getSet[S, A](l: Lens[S, A], s: S): Boolean =
  l.set(l.get(s))(s) == s
```

On the other hand, the `setGet` law states that if you `set` a value, you always `get` the same value back. 
This law guarantees that `set` is actually updating a value `A` inside of `S`.

```scala mdoc:silent
def setGet[S, A](l: Lens[S, A], s: S, a: A): Boolean =
  l.get(l.set(a)(s)) == a
```
