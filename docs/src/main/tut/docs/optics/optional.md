---
layout: docs
title:  "Optional"
section: "optics"
source: "core/src/main/scala/monocle/POptional.scala"
scaladoc: "#monocle.Prism"
---
# Optional

An `Optional` is an Optic used to zoom inside a `Product`, e.g. `case class`, `Tuple`, `HList` or even `Map`.
Unlike the `Lens`, the element that the `Optional` focus on may not exist.

`Optionals` have two type parameters generally called `S` and `A`: `Optional[S, A]` where `S` represents the `Product` and `A` an optional element inside of `S`.

Let's take a simple list with integers.

We can create an `Optional[List[Int], Int]` which zoom from a `List[Int]` to its potential head by supplying a pair of functions:

*   `getOption: List[Int] => Option[Int]`
*   `set: Int => List[Int] => List[Int]`

```tut:silent
import monocle.Optional
val head = Optional[List[Int], Int] {
  case Nil => None
  case x :: xs => Some(x)
}{ a => {
   case Nil => Nil
   case x :: xs => a :: xs
  }
}
```

Once we have an `Optional`, we can use the supplied `nonEmpty` function to know if it matches:

```tut:silent
val xs = List(1, 2, 3)
val ys = List.empty[Int]
```

```tut:book
head.nonEmpty(xs)
head.nonEmpty(ys)
```

We can use the supplied `getOrModify` function to retrieve the target if it matches, or the original value:

```tut:book
head.getOrModify(xs)
head.getOrModify(ys)
```

The function `getOrModify` is mostly use for polymorphic optics.
If you use monomorphic optics, use function `getOption`

We can use the supplied `getOption` and `set` functions:

```tut:book
head.getOption(xs)
head.set(5)(xs)

head.getOption(ys)
head.set(5)(ys)
```

We can also `modify` the target of `Optional` with a function:

```tut:book
head.modify(_ + 1)(xs)
head.modify(_ + 1)(ys)
```

Or use `modifyOption` / `setOption` to know if the update was successful:

```tut:book
head.modifyOption(_ + 1)(xs)
head.modifyOption(_ + 1)(ys)
```

## Laws

```tut:silent
class OptionalLaws[S, A](optional: Optional[S, A]) {

  def getOptionSet(s: S): Boolean =
    optional.getOrModify(s).fold(identity, optional.set(_)(s)) == s

  def setGetOption(s: S, a: A): Boolean =
    optional.getOption(optional.set(a)(s)) == optional.getOption(s).map(_ => a)

}
```

An `Optional` must satisfies all properties defined in `OptionalLaws` in `core` module.
You can check the validity of your own `Optional` using `OptionalTests` in `law` module.

`getOptionSet` states that if you `getOrModify` a value `A` from `S` and then `set` it back in, the result is an object identical to the original one.

`setGetOption` states that if you `set` a value, you always `getOption` the same value back.