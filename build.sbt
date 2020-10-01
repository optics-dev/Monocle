import com.typesafe.tools.mima.plugin.MimaKeys.mimaPreviousArtifacts
import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import sbt.Keys._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(List(
  organization := "com.github.julien-truffaut",
  homepage := Some(url("https://github.com/optics-dev/Monocle")),
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

// shamelessly copied from cats
def scalaVersionSpecificFolders(srcName: String, srcBaseDir: java.io.File, scalaVersion: String) = {
  def extraDirs(suffix: String) =
    List(CrossType.Pure, CrossType.Full)
      .flatMap(_.sharedSrcDir(srcBaseDir, srcName).toList.map(f => file(f.getPath + suffix)))

  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, y)) if y <= 12 =>
      extraDirs("-2.12-")
    case Some((2, y)) if y >= 13 =>
      extraDirs("-2.13+")
    case _ => Nil
  }
}

lazy val buildSettings = Seq(
  scalaVersion       := "2.13.3",
  crossScalaVersions := Seq("2.13.3"),
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
  Compile / unmanagedSourceDirectories ++= scalaVersionSpecificFolders("main", baseDirectory.value, scalaVersion.value),
  Test / unmanagedSourceDirectories ++= scalaVersionSpecificFolders("test", baseDirectory.value, scalaVersion.value),
  scmInfo := Some(ScmInfo(url("https://github.com/optics-dev/Monocle"), "scm:git:git@github.com:optics-dev/Monocle.git")),
)

lazy val catsVersion = "2.1.1"

lazy val cats              = Def.setting("org.typelevel"     %%% "cats-core"                % catsVersion)
lazy val catsFree          = Def.setting("org.typelevel"     %%% "cats-free"                % catsVersion)
lazy val catsLaws          = Def.setting("org.typelevel"     %%% "cats-laws"                % catsVersion)
lazy val alleycats         = Def.setting("org.typelevel"     %%% "alleycats-core"           % catsVersion)
lazy val scalaz            = Def.setting("org.scalaz"        %%% "scalaz-core"              % "7.3.2")
lazy val shapeless         = Def.setting("com.chuusai"       %%% "shapeless"                % "2.3.3")
lazy val refinedDep        = Def.setting("eu.timepit"        %%% "refined"                  % "0.9.15")
lazy val refinedScalacheck = Def.setting("eu.timepit"        %%% "refined-scalacheck"       % "0.9.15" % "test")

lazy val discipline           = Def.setting("org.typelevel"  %%% "discipline-core"          % "1.0.3")
lazy val discipline_scalatest = Def.setting("org.typelevel"  %%% "discipline-scalatest"     % "2.0.0")

lazy val macroVersion = "2.1.1"

lazy val paradisePlugin = Def.setting{
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) if v <= 12 =>
      Seq(compilerPlugin("org.scalamacros" % "paradise" % macroVersion cross CrossVersion.patch))
    case _ =>
      // if scala 2.13.0-M4 or later, macro annotations merged into scala-reflect
      // https://github.com/scala/scala/pull/6606
      Nil
  }
}

lazy val kindProjector  = "org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full

def mimaSettings(module: String): Seq[Setting[_]] = mimaDefaultSettings ++ Seq(
  mimaPreviousArtifacts := Set("com.github.julien-truffaut" %%  (s"monocle-${module}") % "2.0.0")
)

lazy val gitRev = sys.process.Process("git rev-parse HEAD").lineStream_!.head

lazy val scalajsSettings = Seq(
  scalacOptions += {
    lazy val tag = (version in ThisBuild).value
    val s = if (isSnapshot.value) gitRev else tag
    val a = (baseDirectory in LocalRootProject).value.toURI.toString
    val g = "https://raw.githubusercontent.com/optics-dev/Monocle"
    s"-P:scalajs:mapSourceURI:$a->$g/$s/"
  },
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-maxSize", "8", "-minSuccessfulTests", "50")
)

lazy val monocleSettings    = buildSettings
lazy val monocleJvmSettings = monocleSettings
lazy val monocleJsSettings  = monocleSettings ++ scalajsSettings

lazy val monocle = project.in(file("."))
  .settings(moduleName := "monocle")
  .settings(noPublishSettings)
  .settings(monocleSettings)
  .aggregate(monocleJVM, monocleJS)
  .dependsOn(monocleJVM, monocleJS)

lazy val monocleJVM = project.in(file(".monocleJVM"))
  .settings(monocleJvmSettings)
  .settings(noPublishSettings)
  .aggregate(
    core.jvm, generic.jvm, law.jvm, macros.jvm, state.jvm, refined.jvm, unsafe.jvm, test.jvm,
    example, bench)
  .dependsOn(
    core.jvm, generic.jvm, law.jvm, macros.jvm, state.jvm, refined.jvm, unsafe.jvm, test.jvm % "test-internal -> test",
    bench % "compile-internal;test-internal -> test")

lazy val monocleJS = project.in(file(".monocleJS"))
  .settings(monocleJsSettings)
  .settings(noPublishSettings)
  .aggregate(core.js, generic.js, law.js, macros.js, state.js, refined.js, unsafe.js, test.js)
  .dependsOn(core.js, generic.js, law.js, macros.js, state.js, refined.js, unsafe.js, test.js  % "test-internal -> test")

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings),
  )
  .jvmSettings(mimaSettings("core"): _*)
  .settings(libraryDependencies ++= Seq(cats.value, catsFree.value))
  .settings(
    moduleName := "monocle-core",
    scalacOptions ~= (_.filterNot(
      Set(
        "-Xfatal-warnings" // Workaround for sbt bug
      )
    ))
  )

lazy val generic = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core)
  .settings(moduleName := "monocle-generic")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .jvmSettings(mimaSettings("generic"): _*)
  .settings(libraryDependencies ++= Seq(cats.value, shapeless.value))

lazy val refined = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core)
  .settings(moduleName := "monocle-refined")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(libraryDependencies ++= Seq(cats.value, refinedDep.value))

lazy val law = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core)
  .settings(moduleName := "monocle-law")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(libraryDependencies ++= Seq(discipline.value))

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
      scalaOrganization.value % "scala-compiler" % scalaVersion.value % "provided",
    ),
    libraryDependencies ++= paradisePlugin.value,
    unmanagedSourceDirectories in Compile += (sourceDirectory in Compile).value / s"scala-${scalaBinaryVersion.value}"
  )

lazy val state = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core)
  .settings(moduleName := "monocle-state")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings),
  )
  .settings(libraryDependencies ++= Seq(cats.value))

lazy val unsafe = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core)
  .settings(moduleName := "monocle-unsafe")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .jvmSettings(mimaSettings("unsafe"): _*)
  .settings(libraryDependencies ++= Seq(cats.value, alleycats.value, shapeless.value))

lazy val test = crossProject(JVMPlatform, JSPlatform).dependsOn(core, generic, macros, law, state, refined, unsafe)
  .settings(moduleName := "monocle-test")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(noPublishSettings: _*)
  .settings(
    libraryDependencies ++= Seq(cats.value, catsLaws.value, shapeless.value, discipline_scalatest.value, refinedScalacheck.value),
    libraryDependencies ++= paradisePlugin.value
  )

lazy val bench = project.dependsOn(core.jvm, generic.jvm, macros.jvm)
  .settings(moduleName := "monocle-bench")
  .settings(monocleJvmSettings)
  .settings(noPublishSettings)
  .settings(libraryDependencies ++= Seq(
    scalaz.value,
    shapeless.value),
    libraryDependencies ++= paradisePlugin.value
  ).enablePlugins(JmhPlugin)

lazy val example = project.dependsOn(core.jvm, generic.jvm, refined.jvm, macros.jvm, state.jvm, test.jvm % "test->test")
  .settings(moduleName := "monocle-example")
  .settings(monocleJvmSettings)
  .settings(noPublishSettings)
  .settings(
    libraryDependencies ++= Seq(cats.value, shapeless.value, discipline_scalatest.value),
    libraryDependencies ++= paradisePlugin.value
  )

lazy val docs = project.dependsOn(core.jvm, unsafe.jvm, macros.jvm, example)
  .enablePlugins(BuildInfoPlugin, DocusaurusPlugin, MdocPlugin, ScalaUnidocPlugin)
  .settings(moduleName := "monocle-docs")
  .settings(monocleSettings)
  .settings(noPublishSettings)
  .settings(mdocSettings)
  .settings(buildInfoSettings)
  .settings(scalacOptions ~= (_.filterNot(Set("-Ywarn-unused:imports", "-Ywarn-dead-code"))))
  .settings(
    libraryDependencies ++= Seq(cats.value, shapeless.value),
    libraryDependencies ++= paradisePlugin.value
  )

lazy val buildInfoSettings = Seq(
  buildInfoPackage := "monocle.build",
  buildInfoObject := "info",
  buildInfoKeys := Seq[BuildInfoKey](
    scalaVersion,
    scalacOptions,
    sourceDirectory,
    latestVersion in ThisBuild,
    BuildInfoKey.map(version in ThisBuild) {
      case (_, v) => "latestSnapshotVersion" -> v
    },
    BuildInfoKey.map(moduleName in core.jvm) {
      case (k, v) => "core" ++ k.capitalize -> v
    },
    BuildInfoKey.map(crossScalaVersions in core.jvm) {
      case (k, v) => "core" ++ k.capitalize -> v
    },
    organization in LocalRootProject,
    crossScalaVersions in core.jvm,
  )
)

lazy val mdocSettings = Seq(
  mdoc := run.in(Compile).evaluated,
  scalacOptions --= Seq("-Xfatal-warnings", "-Ywarn-unused"),
  crossScalaVersions := Seq(scalaVersion.value),
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(core.jvm),
  target in (ScalaUnidoc, unidoc) := (baseDirectory in LocalRootProject).value / "website" / "static" / "api",
  cleanFiles += (target in (ScalaUnidoc, unidoc)).value,
  docusaurusCreateSite := docusaurusCreateSite
    .dependsOn(unidoc in Compile)
    .dependsOn(updateSiteVariables in ThisBuild)
    .value,
  docusaurusPublishGhpages :=
    docusaurusPublishGhpages
      .dependsOn(unidoc in Compile)
      .dependsOn(updateSiteVariables in ThisBuild)
      .value,
  scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
    "-doc-source-url", s"https://github.com/optics-dev/Monocle/tree/v${(latestVersion in ThisBuild).value}€{FILE_PATH}.scala",
    "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath,
    "-doc-title", "Monocle",
    "-doc-version", s"v${(latestVersion in ThisBuild).value}"
  )
)

def minorVersion(version: String): String = {
  val (major, minor) =
    CrossVersion.partialVersion(version).get
  s"$major.$minor"
}

val latestVersion = settingKey[String]("Latest stable released version")
latestVersion in ThisBuild := {
  val snapshot = (isSnapshot in ThisBuild).value
  val stable = (isVersionStable in ThisBuild).value

  if (!snapshot && stable) {
    (version in ThisBuild).value
  } else {
    (previousStableVersion in ThisBuild).value.get
  }
}

val updateSiteVariables = taskKey[Unit]("Update site variables")
updateSiteVariables in ThisBuild := {
  val file = (baseDirectory in LocalRootProject).value / "website" / "variables.js"

  val variables =
    Map[String, String](
      "organization" -> (organization in LocalRootProject).value,
      "coreModuleName" -> (moduleName in core.jvm).value,
      "latestVersion" -> (latestVersion in ThisBuild).value,
      "scalaPublishVersions" -> {
        val minorVersions = (crossScalaVersions in core.jvm).value.map(minorVersion)
        if (minorVersions.size <= 2) minorVersions.mkString(" and ")
        else minorVersions.init.mkString(", ") ++ " and " ++ minorVersions.last
      }
    )

  val fileHeader =
    "// Generated by sbt. Do not edit directly."

  val fileContents =
    variables.toList
      .sortBy { case (key, _) => key }
      .map { case (key, value) => s"  $key: '$value'" }
      .mkString(s"$fileHeader\nmodule.exports = {\n", ",\n", "\n};\n")

  IO.write(file, fileContents)
}

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  skip in publish := true
)

// For Travis CI - see http://www.cakesolutions.net/teamblogs/publishing-artefacts-to-oss-sonatype-nexus-using-sbt-and-travis-ci
credentials ++= (for {
  username <- Option(System.getenv().get("SONATYPE_USERNAME"))
  password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
} yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq
