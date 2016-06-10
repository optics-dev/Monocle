---
layout: default
title:  "Release Note"
section: "release_note"
---

# 1.2.2

> 9 June 2016

### Addition

-   add `apply` methods for `Prism` and `Iso` [#354](https://github.com/julien-truffaut/Monocle/pull/354) (thanks to [sellout](https://github.com/sellout))
-   add `unapply` methods for `Prism` and `Iso` [#361](https://github.com/julien-truffaut/Monocle/pull/361) (thanks to [sellout](https://github.com/sellout))
-   add `partial` constructor for `Prism` [#355](https://github.com/julien-truffaut/Monocle/pull/361) (thanks to [sellout](https://github.com/sellout))

### Documentation

-   various improvements from [Unisay](https://github.com/Unisay), [fthomas](https://github.com/fthomas) and [amrhassan](https://github.com/amrhassan)

# 1.2.1

> 13 April 2016

### Addition

-   support for scala.js [#335](https://github.com/julien-truffaut/Monocle/pull/335) (thanks to [japgolly](https://github.com/japgolly))
-   `remove`: delete a value associated with a key in a Map-like container [#341](https://github.com/julien-truffaut/Monocle/pull/341) (thanks to [dabd](https://github.com/dabd))
-   `doubleToFloat`, `bigDecimalToLong` and `bigDecimalToInt` [#312](https://github.com/julien-truffaut/Monocle/pull/312) (thanks to [mikaelv](https://github.com/mikaelv))
-   optics for `Tuple1` [#313](https://github.com/julien-truffaut/Monocle/pull/313) (thanks to [exlevan](https://github.com/exlevan))
-   use random index for `AtTests` and `IndexTests` [#343](https://github.com/julien-truffaut/Monocle/pull/343) (thanks to [pvillega](https://github.com/pvillega))
-   `Each` instance for tuples and case classes with fields of the same type [#347](https://github.com/julien-truffaut/Monocle/pull/347) (thanks to [Astrac](https://github.com/Astrac))

### Bug Fixes

-   fix implicit not found message for `Plated` [#320](https://github.com/julien-truffaut/Monocle/pull/320) (thanks to [aoiroaoino](https://github.com/aoiroaoino))
-   fix `stringToBoolean` such as it satisfies 2nd prism law [#340](https://github.com/julien-truffaut/Monocle/pull/340) (thanks to [hasumedic](https://github.com/hasumedic))
-   fix `stringToLong` such as it satisfies 2nd prism law [#339](https://github.com/julien-truffaut/Monocle/pull/339) (thanks to [pvillega](https://github.com/pvillega))
-   fix bit indexing for `Long` [#343](https://github.com/julien-truffaut/Monocle/pull/343) (thanks to [pvillega](https://github.com/pvillega))

### Upgrades

-   scala 2.11.8
-   scalaz to 7.2.2

# 1.2.0

> 17 December 2015

### Documentation

-   add example, typeclass and faq sections to the website [#314](https://github.com/julien-truffaut/Monocle/pull/314), [#316](https://github.com/julien-truffaut/Monocle/pull/316) 
-   document [SI-7139](https://issues.scala-lang.org/browse/SI-7139) limitation for optics constructor in REPL and tut [#309](https://github.com/julien-truffaut/Monocle/pull/309)

### Bug Fixes

-   generate fresh type-parameter name for `modifyF` [#300](https://github.com/julien-truffaut/Monocle/pull/300)  (thanks to [puffnfresh](https://github.com/puffnfresh))
-   publish `monocle-refined` with all the other modules [#311](https://github.com/julien-truffaut/Monocle/pull/311)
-   publish snapshot automatically for scala 2.10 and 2.11 [#310](https://github.com/julien-truffaut/Monocle/pull/310)

### Upgrades

-   scalaz to 7.2.0
-   refined to 0.3.2
-   macro paradise to 2.1.0

# 1.2.0-M2

> 22 November 2015

Thanks to all the 14 contributors since [1.2.0-M1](https://github.com/julien-truffaut/Monocle/compare/v1.2.0-M1...v1.2.0-M2)

### Addition

-   add `only` `Prism` to match a single value [see](https://github.com/julien-truffaut/Monocle/commit/5f6d414019883045ab5e92ea1a6cc650e1f5e0f5)
-   add `below` `Prism` to lift a `Prism` in a `Traverse` [see](https://github.com/julien-truffaut/Monocle/commit/ed7b067d62891352a2a05e1a570451e3740a2446)
-   add `length` for `Fold` and `Traversal` [#236](https://github.com/julien-truffaut/Monocle/commit/1773e93bfe2a7e229c57fc7915ec3519b8831eee) (thanks to [aoiroaoino](https://github.com/aoiroaoino))
-   add optics for `scalaz.Either3` [#242](https://github.com/julien-truffaut/Monocle/commit/720ff020b08be8d7ffb3f60e3d1377147c1dce50) (thanks to [aoiroaoino](https://github.com/aoiroaoino))
-   add `optNelToList` `Iso` [see](https://github.com/julien-truffaut/Monocle/commit/f24bc89b23924948b2950ffa5e8c0e4bcc9dcef0)
-   add `fromIso` combinator for all optics [#245](https://github.com/julien-truffaut/Monocle/commit/db0c92c7bffcf41e40f5368caafa26b337983a7c)
-   add `left` and `right` methods for `Iso`, `Prism`, `Getter` and `Fold` [#273](https://github.com/julien-truffaut/Monocle/commit/8efd6c85f8a3697abc11feae0a5f1a2ba7fd0a58)
-   add safe down cast from BigInt [#267](https://github.com/julien-truffaut/Monocle/commit/1f3c37be2d3e950b1a84b4f1d05c5468bc80e6da)
-   add `productToTuple` `Iso` between case class and tuple using shapeless [#247](https://github.com/julien-truffaut/Monocle/commit/c82c03c95e33314f1cac442b8e47252d5419af77)
-   add `GenIso.fields` white box that generates the same `Iso` than `productToTuple` with better performances but less IDE support [#297](https://github.com/julien-truffaut/Monocle/pull/297) (thanks to [japgolly](https://github.com/japgolly))
-   add `@PLenses` macro annotation to generate `PLens` for case class with type parameters [#114](https://github.com/julien-truffaut/Monocle/commit/f80ee012971689ec31865b67665e2641429b24fd) (thanks to [exlevan](https://github.com/exlevan))
-   add `Plated` typeclass [#289](https://github.com/julien-truffaut/Monocle/commit/2be8bcf8d51113e6b0230dafd065289767da2f28) (thanks to [puffnfresh](https://github.com/puffnfresh))
-   add optics for `scalaz.Cofree` [#290](https://github.com/julien-truffaut/Monocle/commit/a1b71065b36ddac7a94118b04748628d1dcc260c) (thanks to [LiamGoodacre](https://github.com/LiamGoodacre))

### Non backward compatible change
-   change mega imports from package object to `all` object, e.g. `import monocle.function._` becomes `import monocle.function.all._` [#243](https://github.com/julien-truffaut/Monocle/pull/243)
-   change `At` definition from `def at(index: I): Lens[S, Option[A]]` to `def at(index: I): Lens[S, A]`
-   change `At` instance for `Set` and `ISet` from `Lens[S, Option[Unit]]` to `Lens[S, Boolean]`
-   remove `Index` instances for bit indexing primitive `Long`, `Int`, `Char`, `Bye` from `monocle-core`
-   add `monocle-refined` module with `At` instances for bit indexing primitive `Long`, `Int`, `Char`, `Bye` [#291](https://github.com/julien-truffaut/Monocle/pull/291) (thanks to [fthomas](https://github.com/fthomas) and [julien-truffaut](https://github.com/julien-truffaut))

### Deprecation
-   deprecate `theseDisjunction` to `theseToDisjunction` [see](https://github.com/julien-truffaut/Monocle/commit/bde69571074ef6dc08c3d240154feaa40aaaece5)
-   deprecate `nelAndOneIso` to `nelToOneAnd` [see](https://github.com/julien-truffaut/Monocle/commit/f24bc89b23924948b2950ffa5e8c0e4bcc9dcef0)
-   deprecate `sum` to `choice` and `product` to `split` [#239](https://github.com/julien-truffaut/Monocle/commit/f6b163b1702bef046f26576d2e182f32352b88d7)

### Documentation
-   add tut examples for `Prism` [#228](https://github.com/julien-truffaut/Monocle/commit/c65a0c4617b41b0a9f31674516c674efc5d0feb0)
-   add tut examples for `Iso` [#279](https://github.com/julien-truffaut/Monocle/commit/61a526717417728ab4ad92fa9ba10aa0267a58d6) (thanks to [justjoheinz](https://github.com/justjoheinz))
-   add examples for Http Request optics usage [#262](https://github.com/julien-truffaut/Monocle/commit/234097ce1f8601eab8ab47e6610d56aea59acce4) (thanks to [1ambda](https://github.com/1ambda))
-   add learning resources to the website [#251](https://github.com/julien-truffaut/Monocle/commit/0bc53359e799e1124ad5a8c0f90ae9d85bd690d9)

### Bug Fixes
-   fix long parser [#244](https://github.com/julien-truffaut/Monocle/commit/1cdfc0fae44df71700f4d53cfbf23d0f9575ee07) (thanks to [NightRa](https://github.com/NightRa))
-   fix `GenIso` case class with type parameter [#263](https://github.com/julien-truffaut/Monocle/commit/fa6dc7d164142c4fae1ec8014f3eb8c4f9619191)

### Optimisation
-   improve performances of `index` for `Vector` [#258](https://github.com/julien-truffaut/Monocle/commit/d4a29c279ed78c3acd8b253cb525cb71e8656ee4) (thanks to [spebbe](https://github.com/spebbe))

### Upgrades
-   scala to 2.10.6
-   scalaz to 7.1.4
-   shapeless to 2.2.5
-   macro-compat to 1.1.0
-   macro paradise to 2.1.0-M5

# 1.2.0-M1
-   laws definition move to `core` module [see](https://github.com/julien-truffaut/Monocle/tree/master/core/src/main/scala/monocle/law).
    Properties are still defined in `law` module with [discipline](https://github.com/typelevel/discipline)
-   optics defined for `scalaz.Validation` [#211](https://github.com/julien-truffaut/Monocle/pull/211) (thanks to [anakos](https://github.com/anakos))
-   `hListAt` is now public [see](https://github.com/julien-truffaut/Monocle/commit/b8567a677dc70e3db8548c421f487e213f206946)
-   add basic state support for `Lens` in an experimental `state` module [see](https://github.com/julien-truffaut/Monocle/commit/ee2dbd70cc47693615ad539b2dfd5d9c09bcd2be)
-   add `void` for `Optional`, `Traversal`, `Fold` and `Setter` [see](https://github.com/julien-truffaut/Monocle/commit/03f199847a1f572891f48e3e5aef080631f87e2c) 
-   add `Setter` constructor using `scalaz.Contravariant` and `scalaz.Profunctor` [see](https://github.com/julien-truffaut/Monocle/commit/d50ec207b0c0b92061e2ad23de4bcaa841874a75)
-   `GenIso` for object and empty case classes [#219](https://github.com/julien-truffaut/Monocle/pull/219) and [#223](https://github.com/julien-truffaut/Monocle/pull/223) (thanks to [adelbertc](https://github.com/adelbertc))
-   add `optionToDisjunction` `Iso` [#226](https://github.com/julien-truffaut/Monocle/pull/226) (thanks to [aoiroaoino](https://github.com/aoiroaoino))
-   add monomorphic optics for `Option`, `Either`, `scalaz.Disjunction`, `scalaz.Validation` [#181](https://github.com/julien-truffaut/Monocle/issues/181)

### Build
- publish snapshot automatically [#207](https://github.com/julien-truffaut/Monocle/issues/207)  
- create basic web site using compile time verified examples with `tut` [#227](https://github.com/julien-truffaut/Monocle/pull/227)

### Bug Fixes
-   [#205](https://github.com/julien-truffaut/Monocle/issues/205) 

### Upgrades
-   scala to 2.10.5 and 2.11.7
-   scalaz to 7.1.3
-   shapeless to 2.2.3
-   kind projector to 0.6.0