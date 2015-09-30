---
layout: default
title:  "Iso"
section: "optics"
scaladoc: "http://julien-truffaut.github.io/Monocle/api/#monocle.PLens"
source: "https://github.com/julien-truffaut/Monocle/blob/master/example/src/main/scala/monocle/example/LensExample.scala"
pageSource: "https://raw.githubusercontent.com/julien-truffaut/Monocle/master/docs/src/main/tut/iso.md"
---

# Iso

An `Iso` is an Optic which converts elements of type `A` into elements of type
`B` without loss.

Consider these two case classes:

```tut:silent
case class Person(name: String, age: Int)
case class Pers(n: String, a: Int)
```

In order to create an `Iso` between `Person` and `Pers` we need to supply two total functions

* get : Person => Pers
* reverseGet : Pers => Person

```tut:silent
import monocle.Iso
val personPrism = Iso[Person, Pers]((p: Person) => Pers(p.name, p.age))((p: Pers) => Person(p.n, p.a))
```

and thereby create a lossless conversion between these two types. Other usages include for example the transformation of different types of physical units.

It is important to understand that the transformation between type `A` and `B` works for any type `A` and `B` and that the functions `get` and `reverseGet` are true inverses to each other.
