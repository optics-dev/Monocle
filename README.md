Monocle
=======

Monocle is a Scala lens library greatly inspired by Haskell [Lens](https://github.com/ekmett/lens)

Examples of lens and traversal can be found in the sub-project examples

## Using Monocle

```scala
resolvers ++= Seq(
  "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"      % "0.1-SNAPSHOT"
)
```