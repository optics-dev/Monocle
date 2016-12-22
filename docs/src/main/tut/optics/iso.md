---
layout: docs
title:  "Iso"
section: "optics_menu"
source: "core/src/main/scala/monocle/PIso.scala"
scaladoc: "#monocle.Iso"
---
# Iso

An `Iso` is an optic which converts elements of type `S` into elements of type `A` without loss.

Consider a case class `Person` with two fields:

```tut:silent
case class Person(name: String, age: Int)
```

`Person` is equivalent to a tuple `(String, Int)` and a tuple `(String, Int)` is equivalent to `Person`.
So we can create an `Iso` between `Person` and `(String, Int)` using two total functions:

* `get: Person => (String, Int)`
* `reverseGet (aka apply): (String, Int) => Person`

```tut:silent
import monocle.Iso
val personToTuple = Iso[Person, (String, Int)](p => (p.name, p.age)){case (name, age) => Person(name, age)}
```

```tut:book
personToTuple.get(Person("Zoe", 25))
personToTuple.reverseGet(("Zoe", 25))
```

Or simply:

```tut:book
personToTuple(("Zoe", 25))
```

Another common use of `Iso` is between collection. `List` and `Vector` represent the same concept, they are both an 
ordered sequence of elements but they have different performance characteristics. Therefore, we can define an `Iso` between
a `List[A]` and a `Vector[A]`:

```tut:silent
def listToVector[A] = Iso[List[A], Vector[A]](_.toVector)(_.toList)
```

```tut:book
listToVector.get(List(1,2,3))
```

We can also `reverse` an `Iso` since it defines a symmetric transformation:

```tut:book
def vectorToList[A] = listToVector[A].reverse

vectorToList.get(Vector(1,2,3))
```

`Iso` are also convenient to lift methods from one type to another, for example a `String` can be seen as a `List[Char]`
so we should be able to transform all functions `List[Char] => List[Char]` into `String => String`:

```tut:silent
val stringToList = Iso[String, List[Char]](_.toList)(_.mkString(""))
```

```tut:book
stringToList.modify(_.tail)("Hello")
```

## Iso Generation

We defined several macros to simplify the generation of `Iso` between a case class and its `Tuple` equivalent. All macros
are defined in a separate module (see [modules](../modules.html)).

```tut:silent
case class MyString(s: String)
case class Foo()
case object Bar

import monocle.macros.GenIso
```

First of all, `GenIso.apply` generates an `Iso` for `newtype` i.e. case class with a single type parameter:

```tut:book
GenIso[MyString, String].get(MyString("Hello"))
```

Then, `GenIso.unit` generates an `Iso` for object or case classes with no field:

```tut:book
GenIso.unit[Foo]
GenIso.unit[Bar.type]
```

Finally, `GenIso.fields` is a whitebox macro which generalise `GenIso.apply` to all case classes:

```tut:book
GenIso.fields[Person].get(Person("John", 42))
```

Be aware that whitebox macros are not supported by all IDEs.

## Laws

An `Iso` must satisfy all properties defined in `IsoLaws` from the `core` module.
You can check the validity of your own `Iso` using `IsoTests` from the `law` module.

In particular, an `Iso` must verify that `get` and `reverseGet` are inverse. This is done via
`roundTripOneWay` and `roundTripOtherWay` laws:

```tut:silent
def roundTripOneWay[S, A](i: Iso[S, A], s: S): Boolean =
  i.reverseGet(i.get(s)) == s
  
def roundTripOtherWay[S, A](i: Iso[S, A], a: A): Boolean =
  i.get(i.reverseGet(a)) == a
```
