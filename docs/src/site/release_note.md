---
layout: default
title:  "Release Note"
section: "release_note"
---

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