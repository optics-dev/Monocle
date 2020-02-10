addSbtPlugin("com.eed3si9n"       % "sbt-unidoc"                    % "0.4.3")
addSbtPlugin("com.geirsson"       % "sbt-ci-release"                % "1.5.2")
addSbtPlugin("com.typesafe"       % "sbt-mima-plugin"               % "0.6.4")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"                       % "0.3.7")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "1.0.0")
addSbtPlugin("org.portable-scala" % "sbt-crossproject"              % "1.0.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "1.0.0")
addSbtPlugin("com.47deg"          % "sbt-microsites"                % "0.9.4")
addSbtPlugin("org.tpolecat"       % "tut-plugin"                    % "0.6.13")

scalacOptions += "-deprecation"
