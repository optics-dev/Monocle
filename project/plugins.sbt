addSbtPlugin("com.eed3si9n"       % "sbt-buildinfo"   % "0.10.0")
addSbtPlugin("com.eed3si9n"       % "sbt-unidoc"      % "0.4.3")
addSbtPlugin("com.geirsson"       % "sbt-ci-release"  % "1.5.7")
addSbtPlugin("com.typesafe"       % "sbt-mima-plugin" % "0.9.2")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"         % "0.4.2")

val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("1.5.1")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % scalaJSVersion)
addSbtPlugin("org.portable-scala" % "sbt-crossproject"         % "1.0.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"             % "2.4.2")
addSbtPlugin("org.scalameta"      % "sbt-mdoc"                 % "2.2.21")

scalacOptions += "-deprecation"
