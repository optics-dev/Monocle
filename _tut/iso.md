---
layout: default
title:  "Iso"
section: "optics"
scaladoc: "http://julien-truffaut.github.io/Monocle/api/#monocle.PLens"
source: "https://github.com/julien-truffaut/Monocle/blob/master/example/src/main/scala/monocle/example/LensExample.scala"
pageSource: "https://raw.githubusercontent.com/julien-truffaut/Monocle/master/docs/src/main/tut/iso.md"
---

# Iso

An `Iso` is an Optic which converts elements of type `S` into elements of type `A` without loss.

Consider these two case classes:

```scala
case class Person(name: String, age: Int)
case class Pers(n: String, a: Int)
```

In order to create an `Iso` between `Person` and `Pers` we need to supply two total functions:

* `get: Person => Pers`
* `reverseGet: Pers => Person`

```scala
import monocle.Iso
val personToPers = Iso[Person, Pers]((p: Person) => Pers(p.name, p.age))((p: Pers) => Person(p.n, p.a))
```

```scala
scala> personToPers.get(Person("Zoe", 25))
res0: Pers = Pers(Zoe,25)

scala> personToPers.reverseGet(Pers("Zoe", 25))
res1: Person = Person(Zoe,25)
```

and thereby create a lossless conversion between these two types. We could similarly create an `Iso` between `Person` and `(String, Int)`.

Another common use of `Iso` is between collection with same meaning but different performance characteristics, e.g. `List` and `Vector`:

```scala
def listToVector[A] = Iso[List[A], Vector[A]](_.toVector)(_.toList)
```




```scala
scala> listToVector.get(List(1,2,3))
res2: Vector[Int] = Vector(1, 2, 3)
```

We can also `reverse` an `Iso` since it defines a symmetric transformation:

```scala
scala> def vectorToList[A] = listToVector[A].reverse
vectorToList: [A]=> monocle.PIso[Vector[A],Vector[A],List[A],List[A]]

scala> vectorToList.get(Vector(1,2,3))
res3: List[Int] = List(1, 2, 3)
```

`Iso` are also convenient to lift methods from one type for another, for example a `String` can be seen as a `List[Char]`
so we should be able to transform all functions `List[Char] => List[Char]` into `String => String`:

```scala
val stringToList = Iso[String, List[Char]](_.toList)(_.mkString("")) 
```

```scala
scala> stringToList.modify(_.tail)("Hello")
res4: String = ello
```

## Laws

`Iso` laws specifies that `get` and `reverseGet` are true inverses to each other.

```scala
class IsoLaws[S, A](iso: Iso[S, A]) {

  def roundTripOneWay(s: S): Boolean =
    iso.reverseGet(iso.get(s)) == s

  def roundTripOtherWay(a: A): Boolean =
    iso.get(iso.reverseGet(a)) == a

}
```
