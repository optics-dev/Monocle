import sbt._
import Keys._

import xerial.sbt.Sonatype._
import com.typesafe.tools.mima.plugin.MimaKeys.previousArtifact

import com.typesafe.sbt.pgp.PgpKeys.publishSigned

import sbtrelease.ReleasePlugin._
import sbtrelease.ReleaseStep
import sbtrelease.ReleasePlugin.ReleaseKeys.releaseProcess
import sbtrelease.ReleaseStateTransformations._
import sbtrelease.Utilities._

import pl.project13.scala.sbt.SbtJmh._
import JmhKeys._

object Dependencies {
  val scalaz            = "org.scalaz"      %% "scalaz-core"     % "7.1.1"
  val scalacheck        = "org.scalacheck"  %% "scalacheck"      % "1.12.2"
  val scalazSpec2       = "org.typelevel"   %% "scalaz-specs2"   % "0.4.0"  % "test"
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
  import Dependencies._

  val buildScalaVersion = "2.11.6"
  val previousVersion   = "1.0.0"

  val buildSettings = Seq(
    organization       := "com.github.julien-truffaut",
    scalaVersion       := buildScalaVersion,
    crossScalaVersions := Seq("2.10.4", "2.11.6"),
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
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots"),
      "bintray/non"    at "http://dl.bintray.com/non/maven",
      "bintray/scalaz" at "http://dl.bintray.com/scalaz/releases"
    )
  )

  lazy val defaultSettings = buildSettings ++ publishSettings ++ releaseSettings

  lazy val root: Project = Project(
    "monocle",
    file("."),
    settings = buildSettings ++ noPublishSettings ++ Seq(
      run <<= run in Compile in macros
    )) aggregate(core, law, macros, generic, test, example, bench)

  lazy val core: Project = Project(
    "monocle-core",
    file("core"),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(scalaz),
      addCompilerPlugin(kindProjector),
      previousArtifact := Some("com.github.julien-truffaut"  %  "monocle-core_2.11" % previousVersion)
    )
  )

  lazy val law: Project = Project(
    "monocle-law",
    file("law"),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(scalaz, scalacheck),
      previousArtifact := Some("com.github.julien-truffaut"  %  "monocle-law_2.11" % previousVersion)
    )
  ) dependsOn(core)

  lazy val macros: Project = Project(
    "monocle-macro",
    file("macro"),
    settings = defaultSettings ++ Seq(
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
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(scalaz, shapeless.value),
      previousArtifact := Some("com.github.julien-truffaut"  %  "monocle-generic_2.11" % previousVersion)
    )
  ) dependsOn(core)

  lazy val test: Project = Project(
    "monocle-test",
    file("test"),
    settings = buildSettings ++ noPublishSettings ++ Seq(
      libraryDependencies ++= Seq(scalaz, scalazSpec2, shapeless.value),
      addCompilerPlugin(paradisePlugin)
    )
  ) dependsOn(core, generic ,law, macros)

  lazy val example: Project = Project(
    "monocle-example",
    file("example"),
    settings = buildSettings ++ noPublishSettings ++ Seq(
      libraryDependencies ++= Seq(scalaz, shapeless.value),
      addCompilerPlugin(paradisePlugin) // see: http://stackoverflow.com/q/23485426/463761
    )
  ) dependsOn(core, macros, generic, test % "test->test")

  lazy val bench: Project = Project(
    "monocle-bench",
    file("bench"),
    settings = buildSettings ++ jmhSettings ++ noPublishSettings ++ Seq(
      libraryDependencies ++= Seq(
        "com.github.julien-truffaut" %%  "monocle-core"  % "1.1.0-SNAPSHOT",
        "com.github.julien-truffaut" %%  "monocle-macro" % "1.1.0-SNAPSHOT",
        shapeless.value
      ),
      addCompilerPlugin(kindProjector)
    )
  )

  lazy val publishSettings: Seq[Setting[_]] = Seq(
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishSignedArtifacts,
      setNextVersion,
      commitNextVersion,
      pushChanges
    ),
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
    }) ++ sonatypeSettings

  lazy val publishSignedArtifacts = ReleaseStep(
    action = { st =>
      val extracted = st.extract
      val ref = extracted.get(thisProjectRef)
      extracted.runAggregated(publishSigned in Global in ref, st)
    },
    check = { st =>
      // getPublishTo fails if no publish repository is set up.
      val ex = st.extract
      val ref = ex.get(thisProjectRef)
      Classpaths.getPublishTo(ex.get(publishTo in Global in ref))
      st
    },
    enableCrossBuild = true
  )

  lazy val noPublishSettings = Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false
  )

}
