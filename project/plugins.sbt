addSbtPlugin("com.eed3si9n"       % "sbt-unidoc"                    % "0.4.3")
addSbtPlugin("com.geirsson"       % "sbt-ci-release"                % "1.5.3")
addSbtPlugin("com.typesafe"       % "sbt-mima-plugin"               % "0.7.0")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"                       % "0.3.7")

val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("1.1.0")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % scalaJSVersion)
addSbtPlugin("org.portable-scala" % "sbt-crossproject"              % "1.0.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "1.0.0")
addSbtPlugin("com.47deg"          % "sbt-microsites"                % "0.9.4")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"                  % "2.4.0")
addSbtPlugin("org.scalameta"      % "sbt-mdoc"                      % "2.2.0")

scalacOptions += "-deprecation"
