import sbt._
import Keys._

import xerial.sbt.Sonatype._
import xerial.sbt.Sonatype.SonatypeKeys._

object BuildSettings {
  import MonoclePublishing._
  val buildScalaVersion = "2.11.0"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization       := "com.github.julien-truffaut",
    version            := "0.4-SNAPSHOT",
    scalaVersion       := buildScalaVersion,
    crossScalaVersions := Seq(buildScalaVersion, "2.10.4"),
    scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature",
      "-language:higherKinds", "-language:implicitConversions", "-language:postfixOps"),
    incOptions         := incOptions.value.withNameHashing(true),
    resolvers          += Resolver.sonatypeRepo("releases"),
    resolvers          += Resolver.sonatypeRepo("snapshots")
  ) ++ publishSettings
}

object Dependencies {
  val scalaz            = "org.scalaz"      %% "scalaz-core"               % "7.0.6"
  val shapeless         = "com.chuusai"     %% "shapeless"                 % "2.0.0"
  val scalaCheck        = "org.scalacheck"  %% "scalacheck"                % "1.11.3"
  val scalaCheckBinding = "org.scalaz"      %% "scalaz-scalacheck-binding" % "7.0.6"   % "test"
  val specs2            = "org.specs2"      %% "specs2"                    % "2.3.11"  % "test"
  val scalazSpec2       = "org.typelevel"   %% "scalaz-specs2"             % "0.2"     % "test"
  val scalaReflect      = "org.scala-lang"  %  "scala-reflect"             % BuildSettings.buildScalaVersion
}

object MonocleBuild extends Build {
  import BuildSettings._
  import Dependencies._

  lazy val root: Project = Project(
    "monocle",
    file("."),
    settings = buildSettings ++ Seq(
      publishArtifact := false,
      run <<= run in Compile in core) ++ sonatypeSettings
  ) aggregate(core, generic, law, test, example)

  lazy val core: Project = Project(
    "monocle-core",
    file("core"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(scalaz, scalaReflect),
      addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.0" cross CrossVersion.full)
    )
  )

  lazy val generic: Project = Project(
    "monocle-generic",
    file("generic"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(scalaz, shapeless)
    )
  ) dependsOn(core)

  lazy val law: Project = Project(
    "monocle-law",
    file("law"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(scalaz, scalaCheck)
    )
  ) dependsOn(core)

  lazy val test: Project = Project(
    "monocle-test",
    file("test"),
    settings = buildSettings ++ Seq(
      publishArtifact      := false,
      libraryDependencies ++= Seq(scalaz, shapeless, scalaCheck, scalaCheckBinding, specs2, scalazSpec2)
    )
  ) dependsOn(core, generic ,law)

  lazy val example: Project = Project(
    "monocle-example",
    file("example"),
    settings = buildSettings ++ Seq(
      publishArtifact      := false,
      libraryDependencies ++= Seq(scalaz, shapeless, specs2)
    )
  ) dependsOn(core, generic, test % "test->test")
}

object MonoclePublishing  {

  lazy val publishSettings: Seq[Setting[_]] = Seq(
    pomExtra := {
      <url>https://github.com/julien-truffaut/Monocle</url>
        <licenses>
          <license>
            <name>MIT</name>
            <url>http://opensource.org/licenses/MIT</url>
          </license>
        </licenses>
        <scm>
          <connection>scm:git:github.com/julien-truffaut/Monocle</connection>
          <developerConnection>scm:git:git@github.com:julien-truffaut/Monocle.git</developerConnection>
          <url>github.com:julien-truffaut/Monocle.git</url>
        </scm>
        <developers>
          <developer>
            <id>julien-truffaut</id>
            <name>Julien Truffaut</name>
          </developer>
        </developers>
    }
  ) ++ sonatypeSettings

}
