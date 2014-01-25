import sbt._
import Keys._


object BuildSettings {
  val buildScalaVersion = "2.10.3"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization      := "org.scalalens",
    version           := "0.1",
    scalaVersion      := buildScalaVersion,
    scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature"),
    resolvers         += Resolver.sonatypeRepo("releases"),
    resolvers         += Resolver.sonatypeRepo("snapshots"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.0-M3" cross CrossVersion.full)
  )
}

object Dependencies {
  val scalaz       = "org.scalaz"      %% "scalaz-core"   % "7.0.5"
  val scalaTest    = "org.scalatest"   %% "scalatest"     % "2.0.1-SNAP"   % "test"
  val scalaCheck   = "org.scalacheck"  %% "scalacheck"    % "1.11.1"       % "test"
  val scalaReflect = "org.scala-lang"  %  "scala-reflect" % BuildSettings.buildScalaVersion
  val quasiquotes  = "org.scalamacros" % "quasiquotes" % "2.0.0-M3" cross CrossVersion.full
}

object ScalaLensBuild extends Build {
  import BuildSettings._
  import Dependencies._

  lazy val root: Project = Project(
    "root",
    file("."),
    settings = buildSettings ++ Seq(
      run <<= run in Compile in macros)
  ) aggregate(macros, core, examples)

  lazy val macros: Project = Project(
    "macros",
    file("macros"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(scalaReflect, quasiquotes, scalaz)
    )
  ) dependsOn(core)

  lazy val core: Project = Project(
    "core",
    file("core"),
    settings = buildSettings ++ Seq(
      name := "Lens Core",
      libraryDependencies ++= Seq(scalaz, scalaTest, scalaCheck)
    )
  )

  lazy val examples: Project = Project(
    "examples",
    file("examples"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(scalaz, scalaTest, scalaCheck)
    )
  ) dependsOn(macros, core)
}
