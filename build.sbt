import sbt.Keys._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

Global / onChangedBuildSource := IgnoreSourceChanges

inThisBuild(
  List(
    organization := "com.github.julien-truffaut",
    homepage := Some(url("https://github.com/julien-truffaut/Monocle")),
    licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
    developers := List(
      Developer(
        "julien-truffaut",
        "Julien Truffaut",
        "truffaut.julien@gmail.com",
        url("https://github.com/julien-truffaut")
      ),
      Developer(
        "xuwei-k",
        "Kenji Yoshida",
        " 6b656e6a69@gmail.com",
        url("https://github.com/xuwei-k")
      ),
      Developer(
        "cquiroz",
        "Carlos Quiroz",
        "",
        url("https://github.com/cquiroz")
      )
    )
  )
)

lazy val buildSettings = Seq(
  scalaVersion := "2.13.1",
  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  scalacOptions ++= { if (!isDotty.value) Seq(
      "-encoding",
      "UTF-8",
      "-feature",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:postfixOps",
      "-unchecked",
      "-Xfatal-warnings",
      "-deprecation",
      "-Ywarn-dead-code",
      "-Ywarn-value-discard",
      "-Ywarn-unused:imports",
      "-Ymacro-annotations"
    ) else Nil},
  scalacOptions in (Compile, console) -= "-Ywarn-unused:imports",
  scalacOptions in (Test, console) -= "-Ywarn-unused:imports",
  scmInfo := Some(
    ScmInfo(url("https://github.com/julien-truffaut/Monocle"), "scm:git:git@github.com:julien-truffaut/Monocle.git")
  )
)

lazy val scalatest = Def.setting("org.scalatest" %%% "scalatest" % "3.2.0-M2" % "test")

lazy val crossSettings = Seq(
  crossScalaVersions:= Seq("2.13.1", "0.21.0-RC1")
)

lazy val tagName =
  Def.setting(s"v${if (releaseUseGlobalVersion.value) (version in ThisBuild).value else version.value}")

lazy val gitRev = sys.process.Process("git rev-parse HEAD").lineStream_!.head

lazy val scalajsSettings = Seq(
  scalacOptions += {
    lazy val tag = tagName.value
    val s        = if (isSnapshot.value) gitRev else tag
    val a        = (baseDirectory in LocalRootProject).value.toURI.toString
    val g        = "https://raw.githubusercontent.com/julien-truffaut/Monocle"
    s"-P:scalajs:mapSourceURI:$a->$g/$s/"
  },
  jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-maxSize", "8", "-minSuccessfulTests", "50")
)

lazy val monocleSettings    = buildSettings
lazy val monocleJvmSettings = monocleSettings
lazy val monocleJsSettings  = monocleSettings ++ scalajsSettings

lazy val monocle = project
  .in(file("."))
  .settings(moduleName := "monocle")
  .settings(monocleSettings)
  .aggregate(monocleJVM, monocleJS)
  .dependsOn(monocleJVM, monocleJS)

lazy val monocleJVM = project
  .in(file(".monocleJVM"))
  .settings(monocleJvmSettings)
  .aggregate(core.jvm, macros.jvm)
  .dependsOn(core.jvm, macros.jvm)

lazy val monocleJS = project
  .in(file(".monocleJS"))
  .settings(monocleJsSettings)
  .aggregate(core.js, macros.js)
  .dependsOn(core.js, macros.js)

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .settings(moduleName := "monocle-core")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .jvmSettings(crossSettings)
  .settings(libraryDependencies ++= {if (isDotty.value) List() else Seq(scalatest.value)}) // scalatest isn't available in dotty

lazy val macros = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core)
  .in(file("macro"))
  .settings(moduleName := "monocle-macro")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(
    scalacOptions += "-language:experimental.macros",
    libraryDependencies ++= Seq(
      scalaOrganization.value % "scala-reflect"  % scalaVersion.value,
      scalaOrganization.value % "scala-compiler" % scalaVersion.value % "provided"
    ),
    libraryDependencies ++= Seq(scalatest.value)
  )

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  skip in publish := true
)

addCommandAlias("fmt", "; compile:scalafmt; test:scalafmt; scalafmtSbt")
