addSbtPlugin("com.eed3si9n"       % "sbt-unidoc"           % "0.4.1")
addSbtPlugin("com.github.gseitz"  % "sbt-release"          % "1.0.7")
addSbtPlugin("com.jsuereth"       % "sbt-pgp"              % "1.1.0")
addSbtPlugin("com.typesafe"       % "sbt-mima-plugin"      % "0.1.14")
addSbtPlugin("org.xerial.sbt"     % "sbt-sonatype"         % "2.0")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"              % "0.3.2")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"          % "0.6.21")
addSbtPlugin("org.portable-scala" % "sbt-crossproject"         % "0.3.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.3.0")
addSbtPlugin("org.scala-native"   % "sbt-scala-native"         % "0.3.6")
addSbtPlugin("com.47deg"  % "sbt-microsites"       % "0.7.14")

scalacOptions += "-deprecation"
