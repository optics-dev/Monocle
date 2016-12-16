---
layout: docs
title:  "Modules"
section: "main_menu"
position: 1
---

## Modules

In an attempt to be modular, Monocle is broken up into several modules:

* *core* - contains optics (e.g. `Lens`, `Prism`, `Traversal`) and type class definitions (e.g. `Index`, `Each`, `Plated`) and
  type class instances for standard library types and scalaz data types
* *macro* - macros to simplify the generation of optics
* *laws* - laws for the optics and type classes
* *generic* - optics and type class instances for `HList` and `Coproduct` from [shapeless](https://github.com/milessabin/shapeless)
* *state* - conversion between optics and `State` or `Reader`
* *refined* - optics and type class instances using refinement types from [refined](https://github.com/fthomas/refined)
* *unsafe* - optics that do not fully satisfy laws but that are very convenient. More details [here](unsafe_module.html)
* *tests* - tests that check optics and type class instances satisfy laws
* *bench* - benchmarks using jmh to measure optics performances
* *docs* - source for this website

You can add a module to your build by adding the following line to `libraryDependencies`:

```scala
"com.github.julien-truffaut"  %%  "monocle-${module}" % ${version}
```

Here is the complete list of published artifacts:

```scala
libraryDependencies ++= Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"    % ${version},
  "com.github.julien-truffaut"  %%  "monocle-generic" % ${version},
  "com.github.julien-truffaut"  %%  "monocle-macro"   % ${version},
  "com.github.julien-truffaut"  %%  "monocle-state"   % ${version},
  "com.github.julien-truffaut"  %%  "monocle-refined" % ${version},
  "com.github.julien-truffaut"  %%  "monocle-unsafe"  % ${version},
  "com.github.julien-truffaut"  %%  "monocle-law"     % ${version} % "test"
)
```

You need to replace `${version}` with the version of Monocle you want to use.