Monocle
=======

[![Build Status](https://api.travis-ci.org/julien-truffaut/Monocle.png?branch=master)](https://travis-ci.org/julien-truffaut/Monocle)

Monocle is a Scala lens library greatly inspired by Haskell [Lens](https://github.com/ekmett/lens)

See sub-project examples for ... examples ;)


```scala
resolvers ++= Seq(
  "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"  % "0.1" // or 0.2-SNAPSHOT
)
```