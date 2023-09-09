addSbtPlugin("org.typelevel"      % "sbt-typelevel-ci-release" % "0.5.0")
addSbtPlugin("com.eed3si9n"       % "sbt-buildinfo"            % "0.11.0")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"                  % "0.4.5")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "1.13.2")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "1.3.2")
addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.4.15")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"                  % "2.5.0")
addSbtPlugin("org.scalameta"      % "sbt-mdoc"                      % "2.3.7")

scalacOptions += "-deprecation"
