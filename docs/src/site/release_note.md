---
layout: default
title:  "Release Note"
section: "release_note"
---

# 1.3.2

> 02 November 2016

-   add support for scala 2.12.0 [#408](https://github.com/julien-truffaut/Monocle/pull/408)
-   use `Free.roll` in `Plated[Free[S, A]]` instance [#404](https://github.com/julien-truffaut/Monocle/pull/404) (thanks to [aoiroaoino ](https://github.com/aoiroaoino))
-   update build settings for scala.js [#406](https://github.com/julien-truffaut/Monocle/pull/406) (thanks to [aoiroaoino ](https://github.com/aoiroaoino))

# 1.3.1

> 21 October 2016

-   add support for scala 2.12.0-RC2

# 1.3.0

> 18 October 2016

### Addition

-   add `unsafe` module with `select` [#394](https://github.com/julien-truffaut/Monocle/pull/394) (thanks to [cesartl](https://github.com/cesartl))
-   refactor optics laws to use random functions [#357](https://github.com/julien-truffaut/Monocle/pull/357)
-   add `Wrapped` typeclass [#365](https://github.com/julien-truffaut/Monocle/pull/394) (thanks to [puffnfresh](https://github.com/puffnfresh))
-   add `State` syntax for `Optional` [#387](https://github.com/julien-truffaut/Monocle/pull/387) (thanks to [cb372](https://github.com/cb372))
-   add `transformM` and `transformCounting` to `Plated` [#391](https://github.com/julien-truffaut/Monocle/pull/391) (thanks to [arkadius](https://github.com/arkadius))
-   add `applyN` for `Traversal` [#379](https://github.com/julien-truffaut/Monocle/pull/379) (thanks to [jule64](https://github.com/jule64)) 
-   add `mapping` [#396](https://github.com/julien-truffaut/Monocle/pull/396) (thanks to [mdulac](https://github.com/mdulac)) 

### Deprecation

-   remove all deprecated elements from 1.1 e.g. (`Lenser`, `headMaybe`, `getMaybe`, `setMaybe`, `modifyMaybe`) ([see](https://github.com/julien-truffaut/Monocle/commit/ff65c967096e7c1243119075ba35e46f12728f71))

### Upgrades

| dependencies  | monocle 1.2.2 | monocle 1.3.0 |
| ------------- | -------------:| -------------:|
|        scalaz |         7.2.2 |         7.2.6 |
|     shapeless |         2.2.5 |         2.3.2 |
|       refined |         0.3.2 |         0.5.0 |
|    discipline |           0.4 |           0.7 |

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

> 06 July 2015

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

# 1.1.0

> 31 Mars 2015

### All Optics are now abstract classes
-   easier to change implementation and maintain backward compatibility
-   offer faster implementation using macros or `new` (not recommended)

### Deprecate use of `Maybe` and `IList` in interface
-   1.0.0 replaced `Option` by `scalaz.Maybe` but it turns out that `Maybe` advantages are not worth the cost of moving away from scala std.
-   same between `List` and `scalaz.IList`

### Add `Category` related instances and methods
-   type classes e.g. `Compose`, `Category`, `Arrow`, `Choice`
-   methods e.g. `id`, `codiagonal`, `first`, `second`, `sum`, `product`

### Macro
-   `monocle.macros.GenIso` generates `Iso` for case class with a single accessor
-   `monocle.macros.GenPrism` generates `Prism` for sealed trait
-   `@Lenses` now supports case classes with type parameters [see](https://github.com/julien-truffaut/Monocle/blob/697bbf0ca3cbb1e8e8b3a63626fc45dfca3cd3cf/example/src/test/scala/monocle/LensExample.scala#L62)
-   `Lenser` is deprecated, use `GenLens` instead (same functionality but more consistant naming)

### Spark friendly
-   Optics and type classes extends `Serializable`

### Syntax
-   using optics as an infix operator operator requires a single import `monocle.syntax.apply` or `monocle.syntax._` 

# 1.0.0

> 14 December 2014

### Rename Optics
-   `Iso`, `Lens`, `Prism`, `Optional`, `Traversal` and `Setter` were prepended by a P for Polymorphic
-   `SimpleIso`, `SimpleLens`, `SimplePrism`, `SimpleOptional`, `SimpleTraversal` and `SimpleSetter` lost `Simple`
-   for example, `Lens` was renamed to `PLens` and `SimpleLens` to `Lens`
-   benefits: in practice most optics are not polymorphic, so it is more convenient to have a shorter name for the most used optics.

### No inheritance between Optics
-   All Optics use `asX` methods to transform Optics, e.g. `Prism[S, T, A, B]` has methods `asOptional: Optional[S, T, A, B]`, `asTraversal: Traversal[S, T, A, B]`, ... methods

### Compose direction
-   in 0.5, `composeY` meant that the result of the composition is an Optic of type `Y`
-   in 1.0, `composeY` means that you compose with an Optic of type `Y`

### Main method changes
-   removed `set`, `modify`, `setOption`, `modifyOption`
-   `setF` became `set`
-   `setFOption` became `setMaybe`
-   `modifyF` became `modify`
-   `modifyFOption` became `modifyMaybe`
-   `modifyF` became `lift` or `multiLift` depending on the Optic
-   `getOption` became `getMaybe`

### Simplified Constructors
-   Curry Optics constructor and merged Simple Optic constructors
-   Shuffle methods and parameters order to be consistent between Optics

### Use Maybe and IList from scalaz instead of Option and List from std
-   `Maybe` can be transformed to `Option` by calling `toOption` 
-   `IList` can be transformed to `List` by calling `toList` 
-   benefits: no variance, better scalaz support, safer methods

### Simplified type classes
-   merged `HeadOption` and `TailOption` into `Cons`
-   merged `Head` and `Tail` into `Cons1`
-   merged `InitOption` and `LastOption` into `Snoc`
-   merged `Init` and `Last` into `Snoc1`
-   removed `SafeCast` as it was simply a way to summon `Prism` implicitly
-   removed `AtBit` and defined `Index[S, Int, Boolean]` where `S` is a numeric type

### Macros
-   all macros have been moved to `monocle.macros` package
-   `mkLens` has been moved to `monocle.macros.internal`, i.e. it is not public anymore. Use `Lenser` or `@Lenses`

# 0.5.1

> 29 September 2014

### Macros

-   Add `@Lenses` Macro annotation to generate `SimpleLens` automatically for case classes
-   Add `Lenser` Macro to generate `SimpleLens` with a better IDE support than Macro annotation
-   Deprecate `mkLens` Macro as `Lenser` is strictly more powerful  

### Bug Fix

-   [Fix](https://github.com/julien-truffaut/Monocle/commit/a544743ca439b485ac2be178f290a85bbf6c2c80) `SafeCast` between `String` to `Int`

# 0.5.0

> 03 August 2014

### Build and Dependencies

-   Upgrade scalaz dependency from 7.0.6 to 7.1
-   Upgrade specs2 dependency from 2.3.11 to 2.4
-   Build with scala 2.11.2
-   Use sbt-typelevel plugin

### Breaking Changes

-   SimpleOptional apply method takes a setter function from `(S, A) => A` instead of `(S, Option[A]) => A`
-   Polymorphic optics instance reorganisation: All instances has been move from the companion objects of the
    polymorphic optics to packages:
    -   `monocle.std` for standard Scala and scalaz classes
    -   `monocle.generic` for shapeless classes
    
    Check out this [example](../example/src/test/scala/other/ImportExample) to see how it affects imports
-   reverseModify has been moved from Monocle package object to `Prism` syntax
    
### New Features

-   Add setOption and modifyOption for `Optional`
-   Flipped curried version of `set => setF`, `modify => modifyF`, `setOption => setOptionF`, `modifyOption => modifyOptionF`
    See [example](../example/src/test/scala/monocle/LensExample.scala#L43)
-   New constructors for simple optics with better type inference, see [example](../test/src/test/scala/monocle/LensSpec.scala#L13-L16)    
-   implicit not found annotations have been added to polymorphic optics to help user figuring out which import is missing
-   `monocle.scalaz.Either` has been renamed `monocle.scalaz.Disjunction` to avoid clashes wih `monocle.std.Either`
-   Add `Each`, `Index`, `Field1`, `Head`, `Tail` and `LastOption` instances for `scalaz.OneAnd`
-   Add `Each`, `HeadOption`, and `LastOption` for `scala.Some`

# 0.4.0

> 28 May 2014

### Modularity and build
-   Cross build for scala 2.10.4 and 2.11.0
-   Move Optic Laws from module core to law module
-   Move `Macro` from module core to macro module
-   Update dependencies
    -   Remove scalacheck, scala-reflect and scala-compiler dependencies from core
    -   upgrade scalacheck to 1.11.3
-   Add Mima support on core to check for binary compatibility between minor version for next releases

### New major concept: Optional
-   Add `Optional` a new major concept that represents a 0-1 `Traversal`. `Optional` is a sub class of `Traversal` and
    a super class of `Lens` and `Prism`. Composing a `Lens` and a `Prism` creates an `Optional` instead of a `Traversal`,
    hence we maintain information that the resulting Optic zooms to an optional object and not a list
-   Rename `Head` -> `HeadOption`, `Last` -> `LastOption`, `Tail` -> `TailOption`and `Init` -> `InitOption`
-   Change `HeadOption`, `LastOption`, `TailOption`, `InitOption` Optic from `Traversal` to `Optional`

### Functions
-   Generalize `Reverse`, `Tail` and `Init` from 1 to 2 types parameters.
-   Add `Head`, `Last`, `Tail`, `Init` functions with the same semantic than the Option (`HeadOption`, ...) variant except
    that their Optic is a `Lens` and not an `Optional`. This implies that `Head`, `Last`, `Tail`, `Init` can only be defined on
    elements with a mandatory head, last, tail and init. Implemented on 2-6 tuple and `HList`.
-   Implemented `Reverse` instances on all tuple and `HList`.
-   Implemented `Reverse`, `Head`, `Last`, `Tail`, `Init` instances on all tuple and `HList`.
-   Implemented `HeadOption`, `LastOption`, `TailOption`, `InitOption`, `Each`, `Index` and `FilterIndex` for scalaz `IList`.

### Miscellaneous
-   Add reverseModify on `SimplePrism` as an alias for `getOption map modify`.
-   Add syntax for `Getter`, `Fold` and `Setter`.
-   Add non symbolic functions to use Optics as operators, e.g.
    `List(1,2,3) applyTraversal each getAll` instead of `List(1,2,3) |->> each getAll`
-   Add import for all syntax: `import monocle.syntax._`


# 0.3.0

> 02 May 2014

-    Update dependencies
    -    scala to 2.10.4
    -    shapeless to 2.0.0
    -    macro paradise to 2.0.0
    -    quasi quotes to 2.0.0
-    Add apply4 to apply6 methods to Traversal
-    Add `Each` instance for 4 to 6 tuple of same type
-    Generic sub module:
    -    Add `Field1` to `Field6` instances for `HList`
    -    Add `SafeCast` instance for `Coproduct`, hence supporting Prism for sealed family
    -    Remove `_1`, `_2`, `_3` Lens using shapeless `Generic`, since composing `toHList` Iso and `Fields` Lens is equivalent

# 0.2.0

> 27 April 2014

-   Add generic Lens, Traversal, Iso and Prism in monocle.function:
    -   `_1`, `_2`, ..., `_6`, Lens to focus on the ith element of a tuple. Implemented for tuple of size 6 or less
    -   `atBit(i: Int)`, Lens to focus on ith Bit. Implemented for Boolean, Byte, Char, Int and Long
    -   `safeCast`, Prism defining a safe down casting between two types. Implemented for most primitive type and String
    -   `curry` and `uncurry`, Iso for functions with up to 5 parameters
    -   `head` and `last`, Traversal to the first and last element respectively. Implemented for std List, Vector, Stream, Option and String
    -   `tail` and `init`, Traversal to all elements except first and all elements except last respectively.
        Implemented for std List, Vector, Stream and String
    -   `reverse`, Iso that reverses the order of elements. Implemented for std List, Vector, Stream, String and scalaz Tree
    -   `index(i: Int)`, Traversal to focus on ith element. Implemented for std List, Vector, Stream, Map and String
    -   `each`, Traversal to focus on all elements. Implemented for std List, Vector, Stream, Option, Map, String, pair and triple of same type and scalaz tree
    -   `filterIndex(predicate: Index => Boolean)`, Traversal to focus on all elements with index verifying predicate.
        Implemented for List, Vector, Stream, Map and String
    -   `at(key: K)`, Lens to focus on an optional element at a key, strictly more powerful than index as it permits to
        add and delete elements. Implemented for Map
-   Alias for Iso, Prism, Lens and Traversal composition:
    -   `<->  == composeIso`
    -   `?->  == composePrism`
    -   `|->  == composeLens`
    -   `|->> == composeTraversal`
-   Add experimental sub project generic, using shapeless to generate Iso between `HList` and case class or tuple
-   Add documentation in examples

# 0.1.0

> 27 February 2014

-   First release