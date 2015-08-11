---
layout: default
title:  "Prism"
section: "optics"
scaladoc: "http://julien-truffaut.github.io/Monocle/api/#monocle.PPrism"
pageSource: "https://raw.githubusercontent.com/julien-truffaut/Monocle/master/docs/src/main/tut/prism.md"
---
# Prism

A `Prism` is an Optic used to select part of a `Sum` type (also know as `Coproduct`), e.g. `sealed trait` or `Enum`.

`Prism` have two type parameters generally called `S` and `A`: `Prism[S, A]` where `S` represents the `Sum` and `A` a part of the `Sum`.

Let's take the example of a simple enum:

```tut:silent
sealed trait Day
case object Monday extends Day
case object Tuesday extends Day
// ...
case object Sunday extends Day
```

We can define a `Prism` which only selects `Tuesday`
`Tuesday` is a singleton, so it is isomorphic to `Unit` (type with a single inhabitant):
 
```tut:silent
import monocle.Prism

val _tuesday = Prism[Day, Unit]{
  case Tuesday => Some(())
  case _       => None
}(_ => Tuesday)
```
 
`_tuesday` can then be used as constructor of `Day`:
 
```tut
_tuesday.reverseGet(())
```
 
or as a replacement of pattern matching:
 
```tut
_tuesday.getOption(Monday)
_tuesday.getOption(Tuesday)
```
 
Let's have look at `Prism` toward larger types such as `LinkedList`. 
A `LinkedList` is recursive data type that either empty or a cons, so we can easily define a `Prism` from a `LinkedList`
to each of the two constructors:

```scala
sealed trait LinkedList[A]
case class Nil[A]() extends LinkedList[A]
case class Cons[A](head: A, tail: LinkedList[A]) extends LinkedList[A]

def _nil[A] = Prism[LinkedList[A], Unit]{
  case Nil()      => Some(())
  case Cons(_, _) => None
}(_ => Nil())

def _cons[A] = Prism[LinkedList[A], (A, LinkedList[A])]{
  case Nil()      => None
  case Cons(h, t) => Some((h, t)) 
}{ case (h, t) => Cons(h, t)}
```

```tut:invisible
import monocle.example.PrismExample._ // don't know why it fails to compile if defined in tut
```

```tut:silent
val l1 = Cons(1, Cons(2, Cons(3, Nil())))
val l2 = _nil[Int].reverseGet(())
```

A few usage of `Prism`:

```tut
_cons.getOption(l1)
_cons.isMatching(l1)
_cons[Int].modify(_.copy(_1 = 5))(l1)
_cons[Int].modify(_.copy(_1 = 5))(l2)
```

Contrarily to a `Lens`, a `Prism` can fail so `modify` is noop if a `Prism` fails to match. If you want to know if `modify`
has an effect, you can use `modifyOption` instead:

```tut
_cons[Int].modifyOption(_.copy(_1 = 5))(l1)
_cons[Int].modifyOption(_.copy(_1 = 5))(l2)
```

It is quite annoying that we need to use `copy` to `modify` the first element of a tuple. A tuple is a `Product` so we
should be able to use a `Lens` to zoom further:

```tut
import monocle.function.Fields._ // to have access to first, second, ...
import monocle.std.tuple2._      // to get instance Fields instance for Tuple2

(_cons[Int] composeLens first).set(5)(l1)
(_cons[Int] composeLens first).set(5)(l2)
```

Composing a `Prism` with a `Lens` gives an `Optional` (TODO `Optional` doc).