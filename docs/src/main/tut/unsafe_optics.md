---
layout: default
title:  "Unsafe Optics"
section: "optics"
pageSource: "https://raw.githubusercontent.com/julien-truffaut/Monocle/master/docs/src/main/tut/unsafe_optics.md"
---
# Background

All the Optics defined in the `core` module obey a well defined set of `Laws` that make these Optics safe to use for all possible cases.
Some Optics however cannot make such guarantees and as a result are said to be "unsafe" and placed in the `unsafe` module. 

This is not to say that these Optics cannot be used - they actually come in handy on many occasions - but care must be taken in using them as we will proceed show in the next sections.


# The `unsafe` module

Unsafe Optics are defined in the `unsafe` module.  This module contains the following Optics:

- UnsafeSelect
- UnsafeHCompose


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

## UnsafeHCompose

`UnsafeHCompose` offers the ability to work with *any number* (0 to n) of `Lens` on `S`.  

It is a special case of `Traversal` that is considered unsafe because the (0 to n) of `Lens` requirement breaks the xxxx `Law` of `Traversal`.

