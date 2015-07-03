---
layout: source
title:  "Hello"
section: "example"
scaladoc: "http://julien-truffaut.github.io/Monocle/api/#monocle.PLens"
source: "https://github.com/julien-truffaut/Monocle/blob/master/example/src/main/scala/monocle/example/LensExample.scala"
pageSource: "https://raw.githubusercontent.com/julien-truffaut/Monocle/master/docs/src/main/tut/lens.md"
---
# Lens

A `Lens` is an Optic used to zoom inside a `Product`, e.g. case class, `Tuple`, `HList`.

Let's take a simple example of 2 nested case classes:

```scala
case class Person(name: String, age: Int, address: Address)
case class Address(streetNumber: Int, streetName: String)
```

A `Lens` has two type parameters generally called `S` and `A`: `Lens[S, A]`.
`S` represents the `Product` and `A` an element inside of `S`, in our above example we can define a `Lens` between:
*   `Person` and its field `name`, `age` or `address`
*   `Address` and its field `streetNumber` or `streetName`

```tut
import monocle.Lens
import monocle.example.LensExample._

john

_age: Lens[Person, Int]

_age.get(john)
_age.set(25)(john)
_age.modify(_ + 1)(john)
```

Lenses can be composed to zoom deeper in a data structure

```tut
(_address composeLens _streetNumber).get(john)
(_address composeLens _streetNumber).set(2)(john)
```



