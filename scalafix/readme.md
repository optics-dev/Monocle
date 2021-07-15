# Scalafix rules for Monocle

To develop rule:
```
sbt ~tests/test
# edit rules/src/main/scala/fix/Monocle.scala
```
To run

Add the scalafix plugin to your project/plugins.sbt or to your global plugins.

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.1")
Run

$ sbt ";scalafixEnable; scalafix github:monocle/monocle/Monocle3"