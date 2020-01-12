---
layout: docs
title:  "Unsafe Module"
section: "main_menu"
---

## Unsafe module

The `unsafe` module contains `Optics` that do not fully satisfy the set of `Optics` `Laws` of the `core` module.

These `Optics` hence require additional care from the end user to avoid unlawful usages as those are not enforced by the library.

The module currently defines one `Optic`, `UnsafeSelect`, but more will be added as required.


## UnsafeSelect

`UnsafeSelect` allows to create a `Prism` based on a predicate. Let's have a look at a simple example:

```tut:silent
case class Person(name: String, age: Int)
```

Using an `UnsafeSelect` we can select all `Person` with `age >= 18` and then use a `Lens` to modify their name:

```tut:silent
import monocle.unsafe.UnsafeSelect
import monocle.macros.GenLens

(UnsafeSelect.unsafeSelect[Person](_.age >= 18) andThenLens GenLens[Person](_.name)).modify("Adult " + _)
```

This operator is considered unsafe because it allows for inconsistency if a `Lens` is then used to change one of the values used in the predicates. For example:

```tut:silent
import monocle.unsafe.UnsafeSelect
import monocle.macros.GenLens

(UnsafeSelect.unsafeSelect[Person](_.age >= 18) andThenLens GenLens[Person](_.age)).set(0)
```

In this example the age is reset to `0` which invalidates the original predicate of `age >= 18`. More formally `UnsafeSelect` can invalidate the `roundTripOtherWayLaw` law.
