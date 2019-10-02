addSbtPlugin("com.eed3si9n"       % "sbt-unidoc"                    % "0.4.2")
addSbtPlugin("com.github.gseitz"  % "sbt-release"                   % "1.0.11")
addSbtPlugin("com.geirsson"       % "sbt-ci-release"                % "1.4.31")
addSbtPlugin("com.typesafe"       % "sbt-mima-plugin"               % "0.6.0")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"                       % "0.3.7")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "0.6.29")
addSbtPlugin("org.portable-scala" % "sbt-crossproject"              % "0.6.1")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "0.6.1")
addSbtPlugin("com.47deg"          % "sbt-microsites"                % "0.9.7")
addSbtPlugin("org.tpolecat"       % "tut-plugin"                    % "0.6.13")

scalacOptions += "-deprecation"
