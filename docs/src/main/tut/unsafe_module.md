---
layout: default
title:  "Unsafe Module"
section: "optics"
pageSource: "https://raw.githubusercontent.com/julien-truffaut/Monocle/master/docs/src/main/tut/unsafe_module.md"
---
# Intro

The `unsafe` module contains `Optics` that cannot fully satisfy the set of `Optics` `Laws` of the `core` module.

While these "unsafe" `Optics` are still handy for many common use case, care must be taken by the user to avoid the "unlawful" usage as it is not enforced by the library.

The module currently defines only one `Optic`, `UnsafeSelect`, but more will added as required.


## UnsafeSelect

`UnsafeSelect` allows to create a `Prism` based on a predicate. Let's have a look at a simple example:

```tut:silent
case class Person(name: String, age: Int)
```

Using an `UnsafeSelect` we can select all `Person` with `age >= 18` and then use a `Lens` to modify their name:

```tut:silent
UnsafeSelect.unsafeSelect[Person](_.age >= 18) composeLens GenLens[Person](_.name).modify("Adult " + _)

```

This operator is considered unsafe because it allows for inconsistency if a `Lens` is then used to change one of the value used in the predicates. For example:

```tut:silent
UnsafeSelect.unsafeSelect[Person](_.age >= 18) composeLens GenLens[Person](_.age).set(0)
```

In this example the age is reset to `0` which invalidates the original predicate of `age >= 18`. More formally `UnsafeSelect` can invalidate the `roundTripOtherWayLaw` law.



