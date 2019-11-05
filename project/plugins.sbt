addSbtPlugin("com.github.gseitz"  % "sbt-release"                   % "1.0.11")
addSbtPlugin("com.geirsson"       % "sbt-ci-release"                % "1.4.31")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "0.6.29")
addSbtPlugin("org.portable-scala" % "sbt-crossproject"              % "0.6.1")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "0.6.1")

scalacOptions += "-deprecation"
