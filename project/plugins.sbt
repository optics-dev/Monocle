addSbtPlugin("org.typelevel"      % "sbt-typelevel-ci-release" % "0.7.5")
addSbtPlugin("com.eed3si9n"       % "sbt-buildinfo"            % "0.13.1")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"                  % "0.4.7")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "1.18.1")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "1.3.2")
addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.5.6")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"                  % "2.5.4")
addSbtPlugin("org.scalameta"      % "sbt-mdoc"                      % "2.4.0")

scalacOptions += "-deprecation"
