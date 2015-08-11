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

```scala
sealed trait Day
case object Monday extends Day
case object Tuesday extends Day
// ...
case object Sunday extends Day
```

We can define a `Prism` which only selects `Tuesday`
`Tuesday` is a singleton, so it is isomorphic to `Unit` (type with a single inhabitant):
 
```scala
import monocle.Prism

val _tuesday = Prism[Day, Unit]{
  case Tuesday => Some(())
  case _       => None
}(_ => Tuesday)
```
 
`_tuesday` can then be used as constructor of `Day`:
 
```scala
scala> _tuesday.reverseGet(())
res2: Day = Tuesday
```
 
or as a replacement of pattern matching:
 
```scala
scala> _tuesday.getOption(Monday)
res3: Option[Unit] = None

scala> _tuesday.getOption(Tuesday)
res4: Option[Unit] = Some(())
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




```scala
val l1 = Cons(1, Cons(2, Cons(3, Nil())))
val l2 = _nil[Int].reverseGet(())
```

A few usage of `Prism`:

```scala
scala> _cons.getOption(l1)
res5: Option[(Int, monocle.example.PrismExample.LinkedList[Int])] = Some((1,Cons(2,Cons(3,Nil()))))

scala> _cons.isMatching(l1)
res6: Boolean = true

scala> _cons[Int].modify(_.copy(_1 = 5))(l1)
res7: monocle.example.PrismExample.LinkedList[Int] = Cons(5,Cons(2,Cons(3,Nil())))

scala> _cons[Int].modify(_.copy(_1 = 5))(l2)
res8: monocle.example.PrismExample.LinkedList[Int] = Nil()
```

Contrarily to a `Lens`, a `Prism` can fail so `modify` is noop if a `Prism` fails to match. If you want to know if `modify`
has an effect, you can use `modifyOption` instead:

```scala
scala> _cons[Int].modifyOption(_.copy(_1 = 5))(l1)
res9: Option[monocle.example.PrismExample.LinkedList[Int]] = Some(Cons(5,Cons(2,Cons(3,Nil()))))

scala> _cons[Int].modifyOption(_.copy(_1 = 5))(l2)
res10: Option[monocle.example.PrismExample.LinkedList[Int]] = None
```

It is quite annoying that we need to use `copy` to `modify` the first element of a tuple. A tuple is a `Product` so we
should be able to use a `Lens` to zoom further:

```scala
scala> import monocle.function.Fields._ // to have access to first, second, ...
import monocle.function.Fields._

scala> import monocle.std.tuple2._      // to get instance Fields instance for Tuple2
import monocle.std.tuple2._

scala> (_cons[Int] composeLens first).set(5)(l1)
res11: monocle.example.PrismExample.LinkedList[Int] = Cons(5,Cons(2,Cons(3,Nil())))

scala> (_cons[Int] composeLens first).set(5)(l2)
res12: monocle.example.PrismExample.LinkedList[Int] = Nil()
```

Composing a `Prism` with a `Lens` gives an `Optional` (TODO `Optional` doc).
