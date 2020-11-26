---
id: unsafe_module
title: Unsafe module
---

The `unsafe` module contains `Optics` that do not fully satisfy the set of `Optics` `Laws` of the `core` module.

These `Optics` hence require additional care from the end user to avoid unlawful usages as those are not enforced by the library.

The module currently defines one `Optic`, `UnsafeSelect`, but more will be added as required.


## UnsafeSelect

`UnsafeSelect` allows to create a `Prism` based on a predicate. Let's have a look at a simple example:

```scala mdoc:silent
case class Person(name: String, age: Int)
```

Using an `UnsafeSelect` we can select all `Person` with `age >= 18` and then use a `Lens` to modify their name:

```scala mdoc:silent
import monocle.unsafe.UnsafeSelect
import monocle.macros.GenLens

UnsafeSelect.unsafeSelect[Person](_.age >= 18).andThen(GenLens[Person](_.name)).modify("Adult " + _)
```

This operator is considered unsafe because it allows for inconsistency if a `Lens` is then used to change one of the values used in the predicates. For example:

```scala mdoc:silent
import monocle.unsafe.UnsafeSelect
import monocle.macros.GenLens

UnsafeSelect.unsafeSelect[Person](_.age >= 18).andThen(GenLens[Person](_.age)).set(0)
```

In this example the age is reset to `0` which invalidates the original predicate of `age >= 18`. More formally `UnsafeSelect` can invalidate the `roundTripOtherWayLaw` law.

## MapTraversal

`MapTraversal` provides an `Iso` `allKeyValues` between `Map[K,V]` and `List([K, V])` and Traversal `mapKVTraversal` of `Map[K, V]` to `(K, V)`. They are useful for traversing and modifying the entries of a map.

Both of them are unsafe because of key collision and the unorderness of map entries. As a rule of thumb, laws regarding modifying `(K, V)` and then getting back a `List[(K, V)]` could be broken, while laws regarding modifying `(K, V)` and then getting back a `Map[K, V]` could still hold. For example,

```scala mdoc:silent
import monocle.unsafe.MapTraversal.allKeyValues

val list = List((1, "foo"), (1, "bar"))
val list2 = (allKeyValues[Int, String].get _ compose allKeyValues[Int, String].reverseGet)(list)
```

`list` and `list2` does not equal here due to key collision. Even if there is no key collision, the output list is still not guaranteed to be the original list as the order of map entries may be unspecified.

On the other hand, the composition of `reverseGet` and `get` is identity.

```scala mdoc:silent
import monocle.unsafe.MapTraversal.allKeyValues

val map = Map(1 -> "foo", 1 -> "bar")
val map2 = (allKeyValues[Int, String].reverseGet _ compose allKeyValues[Int, String].get)(map)
```

Here is an example of how to modify the keys of a map with Monocle. Keep in mind creating identical keys can result in surprising behaviour.

```scala mdoc:silent
import cats.implicits._
import monocle.Traversal
import monocle.unsafe.MapTraversal.allKeyValues

val eachL = Traversal.fromTraverse[List, (Int, String)]
def f(x: (Int, String)): (Int, String) = (x._1+1, x._2)

val m = Map(1 -> "foo", 2 -> "bar")
val l = allKeyValues[Int, String].get(m)
val l2 = eachL.modify(f)(l)
val m2 = allKeyValues[Int, String].reverseGet(l2)
```
