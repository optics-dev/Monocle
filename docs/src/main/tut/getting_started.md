---
layout: docs
title:  "Getting Started"
section: "main_menu"
position: 1
---

## Getting Started

To add Monocle to your project, you will need the following dependencies in your `build.sbt` (you probably don't need all modules):

```scala
import sbt._
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.0"         // or "2.11.8", "2.10.6"

val libraryVersion = "1.4.0-M1"  // or "1.4.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"    % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-generic" % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-macro"   % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-state"   % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-refined" % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-unsafe"  % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-law"     % libraryVersion % "test"
)

// for @Lenses macro support
addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
```

## Project Structure

In an attempt to be modular, Monocle is broken up into several modules:

* *core* - contains optics (e.g. `Lens`, `Prism`, `Traversal`) and type class definitions (e.g. `Index`, `Each`, `Plated`) and
  type class instances for standard library types and scalaz data types
* *macro* - macros to simplify the generation of optics
* *laws* - laws for the optics and type classes
* *generic* - optics and type class instances for `HList` and `Coproduct` from [shapeless](https://github.com/milessabin/shapeless)
* *refined* - optics and type class instances using refinement types from [refined](https://github.com/fthomas/refined)
* *unsafe* - optics that do not fully satisfy laws but that are very convenient. More details [here](unsafe_module.html)
* *tests* - tests that check optics and type class instances satisfy laws
* *bench* - benchmarks using jmh to measure optics performances
* *docs* - source for this website
