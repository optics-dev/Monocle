addSbtPlugin("org.typelevel"      % "sbt-typelevel-ci-release" % "0.8.1")
addSbtPlugin("com.eed3si9n"       % "sbt-buildinfo"            % "0.13.1")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"                  % "0.4.8")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "1.20.1")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "1.3.2")
addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.5.8")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"                  % "2.5.5")
addSbtPlugin("org.scalameta"      % "sbt-mdoc"                      % "2.7.2")

scalacOptions += "-deprecation"
