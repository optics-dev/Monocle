---
id: faq
title: FAQ
---

## Which imports are required to use typeclass based optics such as at, each, headOption?

All typeclasses are defined in `monocle.function` package, you can import optic individually with

```scala
import monocle.function.$TYPE_CLASS.$OPTIC
```

For example

```scala
import monocle.function.At.at
import monocle.function.Cons.{headOption, tailOption}
```

or you can import all typeclass based optics with

```scala mdoc:silent
import monocle.function.all._
```

Here is a complete example

```scala mdoc:silent
import monocle.function.all._
import monocle.macros.GenLens

case class Foo(s: String, is: List[Int])
val foo = Foo("Hello", List(1,2,3))

val is = GenLens[Foo](_.is)
```

```scala mdoc
is.composeOptional(headOption).getOption(foo)
```

Note: if you use a version of monocle before 1.4.x, you need another import to get the typeclass instance

```scala mdoc:silent
import monocle.std.list._
```

## What is the difference between at and index? When should I use one or the other?

Both `at` and `index` define indexed optics. However, `at` is a `Lens` and `index` is an `Optional` which means
`at` is stronger than `index`. Let's take the example of a `Map`

```scala mdoc:silent
import monocle.Iso

val m = Map("one" -> 1, "two" -> 2)

val root = Iso.id[Map[String, Int]]
```

```scala mdoc
root.composeOptional(index("two")).set(0)(m)   // update value at index "two"
root.composeOptional(index("three")).set(3)(m) // noop because m doesn't have a value at "three"
root.composeLens(at("three")).set(Some(3))(m)  // insert element at "three"
root.composeLens(at("two")).set(None)(m)       // delete element at "two"
root.composeLens(at("two")).set(Some(0))(m)    // upsert element at "two"
```

In other words, `index` can update any existing values while `at` can also `insert` and `delete`.

Since `index` is weaker than `at`, we can implement an instance of `Index` on more data structure than `At`.
For instance, `List` or `Vector` only have an instance of `Index` because there is no way to insert an element at an
arbitrary index of a sequence.

Note: `root` is a trick to help type inference. Without it, we would get the following error

```scala mdoc:fail
index("two").set(0)(m)
```

The problem is that the compiler does not have enough information to infer the correct `Index` instance. By using
`Iso.id[Map[String, Int]]` as a prefix, we give a hint to the type inference saying we focus on a `Map[String, Int]`.
Similarly, if the `Map` was in a case class, a `Lens` would provide the same kind of hint than `Iso.id`

```scala mdoc:silent
case class Bar(kv: Map[String, Int])
```
```scala mdoc
GenLens[Bar](_.kv).composeOptional(index("two")).set(0)(Bar(m))
```
