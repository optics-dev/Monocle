import sbt.Keys._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

inThisBuild(List(
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
      "NightRa",
      "Ilan Godik",
      "",
      url("https://github.com/NightRa")
    ),
    Developer(
      "aoiroaoino",
      "Naoki Aoyama",
      "aoiro.aoino@gmail.com",
      url("https://github.com/aoiroaoino")
    ),
    Developer(
      "xuwei-k",
      "Kenji Yoshida",
      " 6b656e6a69@gmail.com",
      url("https://github.com/xuwei-k")
    ),
  )
))

lazy val buildSettings = Seq(
  scalaVersion       := "2.13.1",
  crossScalaVersions := Seq("2.13.1"),
  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  scalacOptions     ++= Seq(
    "-encoding", "UTF-8",
    "-feature",
    "-language:implicitConversions", "-language:higherKinds", "-language:postfixOps",
    "-unchecked",
    "-Xfatal-warnings",
    "-deprecation",
    "-Ywarn-dead-code",
    "-Ywarn-value-discard",
    "-Ywarn-unused:imports",
  ),
  scalacOptions ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
    case Some((2, n)) if n <= 12 => Seq("-Xfuture", "-Yno-adapted-args") // TODO Move fatal-warnings and deprecation back to on
    case Some((2, n)) if n >= 13 => Seq("-Ymacro-annotations")
  }.toList.flatten,
  scalacOptions in (Compile, console) -= "-Ywarn-unused:imports",
  scalacOptions in (Test   , console) -= "-Ywarn-unused:imports",
  addCompilerPlugin(kindProjector),
  scmInfo := Some(ScmInfo(url("https://github.com/julien-truffaut/Monocle"), "scm:git:git@github.com:julien-truffaut/Monocle.git"))
)

lazy val scalatest         = Def.setting("org.scalatest"     %%% "scalatest" % "3.2.0-M1" % "test")

lazy val kindProjector  = "org.typelevel"  % "kind-projector" % "0.10.3" cross CrossVersion.binary

lazy val tagName = Def.setting(
 s"v${if (releaseUseGlobalVersion.value) (version in ThisBuild).value else version.value}")

lazy val gitRev = sys.process.Process("git rev-parse HEAD").lineStream_!.head

lazy val scalajsSettings = Seq(
  scalacOptions += {
    lazy val tag = tagName.value
    val s = if (isSnapshot.value) gitRev else tag
    val a = (baseDirectory in LocalRootProject).value.toURI.toString
    val g = "https://raw.githubusercontent.com/julien-truffaut/Monocle"
    s"-P:scalajs:mapSourceURI:$a->$g/$s/"
  },
  jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-maxSize", "8", "-minSuccessfulTests", "50")
)

lazy val monocleSettings    = buildSettings
lazy val monocleJvmSettings = monocleSettings
lazy val monocleJsSettings  = monocleSettings ++ scalajsSettings

lazy val monocle = project.in(file("."))
  .settings(moduleName := "monocle")
  .settings(monocleSettings)
  .aggregate(monocleJVM, monocleJS)
  .dependsOn(monocleJVM, monocleJS)

lazy val monocleJVM = project.in(file(".monocleJVM"))
  .settings(monocleJvmSettings)
  .aggregate(kernel.jvm, dotSyntax.jvm)
  .dependsOn(kernel.jvm, dotSyntax.jvm)

lazy val monocleJS = project.in(file(".monocleJS"))
  .settings(monocleJsSettings)
  .aggregate(kernel.js, dotSyntax.js)
  .dependsOn(kernel.js, dotSyntax.js)

lazy val kernel = crossProject(JVMPlatform, JSPlatform)
  .settings(moduleName := "monocle-kernel")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings),
  )

lazy val dotSyntax = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(kernel)
  .settings(moduleName := "monocle-dot-syntax")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings),
  )
  .settings(libraryDependencies += scalatest.value)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  skip in publish := true
)