import sbt._
import Keys._

import xerial.sbt.Sonatype._
import com.typesafe.tools.mima.plugin.MimaKeys.previousArtifact

import org.typelevel.sbt.TypelevelPlugin._

import pl.project13.scala.sbt.SbtJmh._
import JmhKeys._

object BuildSettings {
  import MonoclePublishing._
  val buildScalaVersion = "2.11.4"
  val previousVersion   = "1.0.0"

  val buildSettings = typelevelDefaultSettings ++ Seq(
    organization       := "com.github.julien-truffaut",
    scalaVersion       := buildScalaVersion,
    crossScalaVersions := Seq("2.10.4", "2.11.4"),
    scalacOptions     ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-language:implicitConversions", "-language:higherKinds", "-language:postfixOps",
      "-unchecked",
      "-Yno-generic-signatures",
      "-Yno-adapted-args",
      "-Ywarn-value-discard"
    ),
    incOptions         := incOptions.value.withNameHashing(true),
    resolvers          += Resolver.sonatypeRepo("releases"),
    resolvers          += Resolver.sonatypeRepo("snapshots"),
    resolvers          += "bintray/non" at "http://dl.bintray.com/non/maven"
  ) ++ publishSettings
}

object Dependencies {
  val scalaz            = "org.scalaz"      %% "scalaz-core"               % "7.1.0"
  val scalaCheckBinding = "org.scalaz"      %% "scalaz-scalacheck-binding" % "7.1.0"  % "test"
  val specs2Scalacheck  = "org.specs2"      %% "specs2-scalacheck"         % "2.4.14"
  val scalazSpec2       = "org.typelevel"   %% "scalaz-specs2"             % "0.3.0"  % "test"
  val shapeless = Def setting (
    CrossVersion partialVersion scalaVersion.value match {
      case Some((2, scalaMajor)) if scalaMajor >= 11 => "com.chuusai" %% "shapeless" % "2.0.0"
      case Some((2, 10))                             => "com.chuusai" %  "shapeless" % "2.0.0" cross CrossVersion.full
    }
  )

  val macroVersion = "2.0.1"
  val paradisePlugin = compilerPlugin("org.scalamacros" %  "paradise"       % macroVersion cross CrossVersion.full)
  val kindProjector  = compilerPlugin("org.spire-math"  %% "kind-projector" % "0.5.2")
}

object MonocleBuild extends Build {
  import BuildSettings._
  import Dependencies._

  lazy val root: Project = Project(
    "monocle",
    file("."),
    settings = buildSettings ++ Seq(
      publishArtifact := false,
      run <<= run in Compile in macros)
  ) aggregate(core, law, macros, generic, test, example, bench)

  lazy val core: Project = Project(
    "monocle-core",
    file("core"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(scalaz),
      addCompilerPlugin(kindProjector),
      previousArtifact     := Some("com.github.julien-truffaut"  %  "monocle-core_2.11" % previousVersion)
    )
  )

  lazy val law: Project = Project(
    "monocle-law",
    file("law"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(scalaz, specs2Scalacheck),
      previousArtifact := Some("com.github.julien-truffaut"  %  "monocle-law_2.11" % previousVersion)
    )
  ) dependsOn(core)

  lazy val macros: Project = Project(
    "monocle-macro",
    file("macro"),
    settings = buildSettings ++ Seq(
      scalacOptions  += "-language:experimental.macros",
      libraryDependencies ++= Seq(
        "org.scala-lang"  %  "scala-reflect"  % scalaVersion.value,
        "org.scala-lang"  %  "scala-compiler" % scalaVersion.value % "provided"
      ),
      addCompilerPlugin(paradisePlugin),
      libraryDependencies ++= CrossVersion partialVersion scalaVersion.value collect {
        case (2, scalaMajor) if scalaMajor < 11 =>
          // if scala 2.11+ is used, quasiquotes are merged into scala-reflect
          Seq("org.scalamacros" %% "quasiquotes" % macroVersion)
      } getOrElse Nil,
      unmanagedSourceDirectories in Compile += (sourceDirectory in Compile).value / s"scala-${scalaBinaryVersion.value}"
    )
  ) dependsOn(core)

  lazy val generic: Project = Project(
    "monocle-generic",
    file("generic"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(scalaz, shapeless.value),
      previousArtifact := Some("com.github.julien-truffaut"  %  "monocle-generic_2.11" % previousVersion)
    )
  ) dependsOn(core)

  lazy val test: Project = Project(
    "monocle-test",
    file("test"),
    settings = buildSettings ++ Seq(
      publishArtifact      := false,
      libraryDependencies ++= Seq(scalaz, scalaCheckBinding, scalazSpec2, specs2Scalacheck, shapeless.value)
    )
  ) dependsOn(core, generic ,law, macros)

  lazy val example: Project = Project(
    "monocle-example",
    file("example"),
    settings = buildSettings ++ Seq(
      publishArtifact      := false,
      libraryDependencies ++= Seq(scalaz, specs2Scalacheck, shapeless.value),
      addCompilerPlugin(paradisePlugin) // Unfortunately necessary :( see: http://stackoverflow.com/q/23485426/463761
    )
  ) dependsOn(core, macros, generic, test % "test->test")

  lazy val bench: Project = Project(
    "monocle-bench",
    file("bench"),
    settings = buildSettings ++ jmhSettings ++ Seq(
      libraryDependencies += shapeless.value,
      addCompilerPlugin(kindProjector)
    )
  ) dependsOn(core, macros)
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
