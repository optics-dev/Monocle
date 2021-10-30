addSbtPlugin("com.eed3si9n"       % "sbt-buildinfo"   % "0.10.0")
addSbtPlugin("com.github.sbt"     % "sbt-unidoc"      % "0.5.0")
addSbtPlugin("com.github.sbt"     % "sbt-ci-release"  % "1.5.10")
addSbtPlugin("com.typesafe"       % "sbt-mima-plugin" % "1.0.1")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"         % "0.4.3")

val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("1.7.1")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % scalaJSVersion)
addSbtPlugin("org.portable-scala" % "sbt-crossproject"         % "1.1.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.1.0")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"             % "2.4.3")
addSbtPlugin("org.scalameta"      % "sbt-mdoc"                 % "2.2.24")

scalacOptions += "-deprecation"
