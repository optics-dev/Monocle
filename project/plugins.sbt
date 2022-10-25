addSbtPlugin("org.typelevel"      % "sbt-typelevel-ci-release" % "0.4.16")
addSbtPlugin("com.eed3si9n"       % "sbt-buildinfo"            % "0.11.0")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"                  % "0.4.3")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.11.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.2.0")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"             % "2.4.6")
addSbtPlugin("org.scalameta"      % "sbt-mdoc"                 % "2.3.6")

scalacOptions += "-deprecation"
