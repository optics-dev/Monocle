---
layout: docs
title:  "Prism"
section: "optics_menu"
source: "core/src/main/scala/monocle/PPrism.scala"
scaladoc: "#monocle.Prism"
---
# Prism

A `Prism` is an optic used to select part of a `Sum` type (also known as `Coproduct`), e.g. `sealed trait` or `Enum`.

`Prisms` have two type parameters generally called `S` and `A`: `Prism[S, A]` where `S` represents the `Sum` and `A` a part of the `Sum`.

Let's take a simplified `Json` encoding:

```scala mdoc:silent
sealed trait Json
case object JNull extends Json
case class JStr(v: String) extends Json
case class JNum(v: Double) extends Json
case class JObj(v: Map[String, Json]) extends Json
```

We can define a `Prism` which only selects `Json` elements built with a `JStr` constructor by supplying a pair of functions:

*   `getOption: Json => Option[String]`
*   `reverseGet (aka apply): String => Json`

```scala mdoc:silent
import monocle.Prism

val jStr = Prism[Json, String]{
  case JStr(v) => Some(v)
  case _       => None
}(JStr)
```

It is common to create a `Prism` by pattern matching on constructor, so we also added `partial` which takes a `PartialFunction`:

```scala mdoc:nest:silent
val jStr = Prism.partial[Json, String]{case JStr(v) => v}(JStr)
```

We can use the supplied `getOption` and `apply` methods as constructor and pattern matcher for `JStr`:

```scala mdoc
jStr("hello")

jStr.getOption(JStr("Hello"))
jStr.getOption(JNum(3.2))
```

A `Prism` can be used in a pattern matching position:

```scala mdoc:silent
def isLongString(json: Json): Boolean = json match {
  case jStr(v) => v.length > 100
  case _       => false
}
```

We can also use `set` and `modify` to update a `Json` only if it is a `JStr`:

```scala mdoc
jStr.set("Bar")(JStr("Hello"))
jStr.modify(_.reverse)(JStr("Hello"))
```

If we supply another type of `Json`, `set` and `modify` will be a no operation:

```scala mdoc
jStr.set("Bar")(JNum(10))
jStr.modify(_.reverse)(JNum(10))
```

If we care about the success or failure of the update, we can use `setOption` or `modifyOption`:

```scala mdoc
jStr.modifyOption(_.reverse)(JStr("Hello"))
jStr.modifyOption(_.reverse)(JNum(10))
```

As all other optics `Prisms` compose together:

```scala mdoc:silent
import monocle.std.double.doubleToInt // Prism[Double, Int] defined in Monocle

val jNum: Prism[Json, Double] = Prism.partial[Json, Double]{case JNum(v) => v}(JNum)

val jInt: Prism[Json, Int] = jNum composePrism doubleToInt
```

```scala mdoc
jInt(5)

jInt.getOption(JNum(5.0))
jInt.getOption(JNum(5.2))
jInt.getOption(JStr("Hello"))
```

## Prism Generation

Generating `Prisms` for subclasses is fairly common, so we added a macro to simplify the process. All macros
are defined in a separate module (see [modules](../modules.html)).
 
```scala mdoc:silent
import monocle.macros.GenPrism

val rawJNum: Prism[Json, JNum] = GenPrism[Json, JNum]
```

```scala mdoc
rawJNum.getOption(JNum(4.5))
rawJNum.getOption(JStr("Hello"))
```

If you want to get a `Prism[Json, Double]` instead of a `Prism[Json, JNum]`, you can compose `GenPrism` 
with `GenIso` (see `Iso` documentation):

```scala mdoc:nest:silent
import monocle.macros.GenIso

val jNum: Prism[Json, Double] = GenPrism[Json, JNum] composeIso GenIso[JNum, Double]
```

```scala
val jNull: Prism[Json, Unit] = GenPrism[Json, JNull.type] composeIso GenIso.unit[JNull.type]
```

A [ticket](https://github.com/julien-truffaut/Monocle/issues/363) currently exists to add a macro to merge these two steps together.

## Prism Laws

A `Prism` must satisfy all properties defined in `PrismLaws` from the `core` module.
You can check the validity of your own `Prisms` using `PrismTests` from the `law` module.

In particular, a `Prism` must verify that `getOption` and `reverseGet` allow a full round trip if the `Prism` matches
i.e. if `getOption` returns a `Some`.

```scala mdoc:silent
def partialRoundTripOneWay[S, A](p: Prism[S, A], s: S): Boolean =
  p.getOption(s) match {
    case None    => true // nothing to prove
    case Some(a) => p.reverseGet(a) == s
  }
  
def partialRoundTripOtherWay[S, A](p: Prism[S, A], a: A): Boolean =
  p.getOption(p.reverseGet(a)) == Some(a)
```
