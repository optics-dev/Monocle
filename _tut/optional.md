---
layout: default
title:  "Optional"
section: "optics"
scaladoc: "http://julien-truffaut.github.io/Monocle/api/#monocle.POptional"
pageSource: "https://raw.githubusercontent.com/julien-truffaut/Monocle/master/docs/src/main/tut/optional.md"
---
# Optional

An `Optional` is an Optic used to zoom inside a `Product`, e.g. `case class`, `Tuple`, `HList` or even `Map`.
Unlike the `Lens`, the element that the `Optional` focus on may do not exist.

`Optionals` have two type parameters generally called `S` and `A`: `Optional[S, A]` where `S` represents the `Product` and `A` an optional element inside of `S`.

Let's take a simple list with integers.

We can create an `Optional[List[Int], Int]` which zoom from a `List[Int]` to its potential head by supplying a pair of functions:

*   `getOption: List[Int] => Option[Int]`
*   `set: Int => List[Int] => List[Int]`

```scala
import monocle.Optional
val _head = Optional[List[Int], Int] {
  case Nil => None
  case x :: xs => Some(x)
}{ a => {
   case Nil => Nil
   case x :: xs => a :: xs
  }
}
```

Once we have an `Optional`, we can use the supplied `getOption` and `set` functions:

```scala
scala> val xs = List(1, 2, 3)
xs: List[Int] = List(1, 2, 3)

scala> _head.getOption(xs)
res0: Option[Int] = Some(1)

scala> _head.set(5)(xs)
res1: List[Int] = List(5, 2, 3)
```

If we use the `Optional` on an empty list:

```scala
scala> val xs = List.empty[Int]
xs: List[Int] = Nil

scala> _head.getOption(xs)
res0: Option[Int] = None

scala> _head.set(5)(xs)
res1: List[Int] = Nil
```
