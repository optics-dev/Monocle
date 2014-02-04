import sbt._
import Keys._


object BuildSettings {
  val buildScalaVersion = "2.10.3"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization      := "org.monocle",
    version           := "0.1",
    scalaVersion      := buildScalaVersion,
    scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature", "-language:higherKinds"),
    resolvers         += Resolver.sonatypeRepo("releases"),
    resolvers         += Resolver.sonatypeRepo("snapshots"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.0-M3" cross CrossVersion.full)
  )
}

object Dependencies {
  val scalaz       = "org.scalaz"      %% "scalaz-core"               % "7.0.5"
  val scalaCheck   = "org.scalacheck"  %% "scalacheck"                % "1.10.1"
  val scalaCheckBinding = "org.scalaz" %% "scalaz-scalacheck-binding" % "7.0.5"        % "test"
  val specs2       = "org.specs2"      %% "specs2"                    % "1.12.3"       % "test"
  val scalazSpec2  = "org.typelevel"   %% "scalaz-specs2"             % "0.1.5"        % "test"
  val scalaReflect = "org.scala-lang"  %  "scala-reflect"             % BuildSettings.buildScalaVersion
  val quasiquotes  = "org.scalamacros" % "quasiquotes"                % "2.0.0-M3" cross CrossVersion.full
  val tests        = Seq(scalaCheck, scalaCheckBinding, specs2, scalazSpec2)
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
      libraryDependencies ++= Seq(scalaReflect, quasiquotes, scalaz) ++ tests
    )
  ) dependsOn(core)

  lazy val core: Project = Project(
    "core",
    file("core"),
    settings = buildSettings ++ Seq(
      name := "Moncole Core",
      libraryDependencies ++= Seq(scalaz) ++ tests
    )
  )

  lazy val examples: Project = Project(
    "examples",
    file("examples"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(scalaz)
    )
  ) dependsOn(macros, core)
}
