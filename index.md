---
layout: default
title:  "Home"
section: "home"
---

Monocle is an `Optics` library where Optics gather the concepts of `Lens`, `Traversal`,
`Optional`, `Prism` and `Iso`. Monocle is strongly inspired by Haskell [Lens](https://github.com/ekmett/lens).

## Getting Started

```scala
import sbt._
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

val scalaVersion   = "2.11.7"    // or "2.10.5"
val libraryVersion = "1.2.0-M1"  // or "1.2.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"    % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-generic" % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-macro"   % libraryVersion,        
  "com.github.julien-truffaut"  %%  "monocle-law"     % libraryVersion % "test" 
)

// for @Lenses macro support
addCompilerPlugin("org.scalamacros" %% "paradise" % "2.0.1" cross CrossVersion.full)
