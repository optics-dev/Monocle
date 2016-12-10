---
layout: docs
title:  "Iso"
section: "optics_menu"
source: "core/src/main/scala/monocle/PIso.scala"
scaladoc: "#monocle.Iso"
---
# Iso

An `Iso` is an Optic which converts elements of type `S` into elements of type `A` without loss.

Consider these two case classes:

```tut:silent
case class Person(name: String, age: Int)
case class Pers(n: String, a: Int)
```

In order to create an `Iso` between `Person` and `Pers` we need to supply two total functions:

* `get: Person => Pers`
* `reverseGet: Pers => Person`

```tut:silent
import monocle.Iso
val personToPers = Iso[Person, Pers]((p: Person) => Pers(p.name, p.age))((p: Pers) => Person(p.n, p.a))
```

```tut
personToPers.get(Person("Zoe", 25))
personToPers.reverseGet(Pers("Zoe", 25))
```

and thereby create a lossless conversion between these two types. We could similarly create an `Iso` between `Person` and `(String, Int)`.

Another common use of `Iso` is between collection with same meaning but different performance characteristics, e.g. `List` and `Vector`:

```scala
def listToVector[A] = Iso[List[A], Vector[A]](_.toVector)(_.toList)
```

```tut:invisible
import monocle.example.IsoExample._
```

```tut
listToVector.get(List(1,2,3))
```

We can also `reverse` an `Iso` since it defines a symmetric transformation:

```tut
def vectorToList[A] = listToVector[A].reverse

vectorToList.get(Vector(1,2,3))
```

`Iso` are also convenient to lift methods from one type for another, for example a `String` can be seen as a `List[Char]`
so we should be able to transform all functions `List[Char] => List[Char]` into `String => String`:

```scala
val stringToList = Iso[String, List[Char]](_.toList)(_.mkString(""))
```

```tut
stringToList.modify(_.tail)("Hello")
```

## Laws

`Iso` laws specifies that `get` and `reverseGet` are true inverses to each other.

```tut:silent
class IsoLaws[S, A](iso: Iso[S, A]) {

  def roundTripOneWay(s: S): Boolean =
    iso.reverseGet(iso.get(s)) == s

  def roundTripOtherWay(a: A): Boolean =
    iso.get(iso.reverseGet(a)) == a

}
```
