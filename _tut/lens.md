---
layout: source
title:  "Hello"
section: "example"
scaladoc: "http://julien-truffaut.github.io/Monocle/api/#core.Lens"
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

```scala
scala> import monocle.Lens
import monocle.Lens

scala> import monocle.example.LensExample._
import monocle.example.LensExample._

scala> john
res0: monocle.example.LensExample.Person = Person(John,20,Address(10,High Street))

scala> _age: Lens[Person, Int]
res1: monocle.Lens[monocle.example.LensExample.Person,Int] = monocle.PLens$$anon$7@553ffce2

scala> _age.get(john)
res2: Int = 20

scala> _age.set(25)(john)
res3: monocle.example.LensExample.Person = Person(John,25,Address(10,High Street))

scala> _age.modify(_ + 1)(john)
res4: monocle.example.LensExample.Person = Person(John,21,Address(10,High Street))
```

Lenses can be composed to zoom deeper in a data structure

```scala
scala> (_address composeLens _streetNumber).get(john)
res5: Int = 10

scala> (_address composeLens _streetNumber).set(2)(john)
res6: monocle.example.LensExample.Person = Person(John,20,Address(2,High Street))
```



