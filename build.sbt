import com.typesafe.tools.mima.core._
import sbt.Keys._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(List(
  organization := "com.github.julien-truffaut",
  homepage := Some(url("https://github.com/optics-dev/Monocle")),
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  developers :=
    List(
      "aoiroaoino" -> "Naoki Aoyama",
      "cquiroz" -> "Carlos Quiroz",
      "kenbot" -> " Ken Scambler",
      "julien-truffaut" -> "Julien Truffaut",
      "NightRa" -> "Ilan Godik",
      "xuwei-k" -> "Kenji Yoshida",
      "yilinwei" -> "Yilin Wei",
    ).map { case (username, fullName) =>
      Developer(username, fullName, s"@$username", url(s"https://github.com/$username"))
    }
  )
)

lazy val kindProjector = "org.typelevel" % "kind-projector" % "0.11.3" cross CrossVersion.full

lazy val buildSettings = Seq(
  scalaVersion := "2.13.3",
  crossScalaVersions := Seq("2.13.3"),
  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  Compile / unmanagedSourceDirectories ++= scalaVersionSpecificFolders("main", baseDirectory.value, scalaVersion.value),
  Test / unmanagedSourceDirectories ++= scalaVersionSpecificFolders("test", baseDirectory.value, scalaVersion.value),
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-feature",
    "-unchecked",
    "-deprecation",
  ) ++ { if(isDotty.value) Seq() else Seq("-Xfatal-warnings") }, // Scala 3 doesn't support -Wconf
  scalacOptions in (Compile, console) -= "-Ywarn-unused:imports",
  scalacOptions ++= {
    if (isDotty.value)
      Seq("-source:3.0-migration", "-Ykind-projector", "-language:implicitConversions,higherKinds,postfixOps")
    else Seq(
      "-Ymacro-annotations",
      "-Ywarn-dead-code",
      "-Ywarn-value-discard",
      "-Ywarn-unused:imports",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:postfixOps",
      "-Wconf:msg=class Cons in package function is deprecated:i",
      "-Wconf:msg=class Cons1 in package function is deprecated:i",
      "-Wconf:msg=class Curry in package function is deprecated:i",
      "-Wconf:msg=class Empty in package function is deprecated:i",
      "-Wconf:msg=class Field1 in package function is deprecated:i",
      "-Wconf:msg=class Field2 in package function is deprecated:i",
      "-Wconf:msg=class Field3 in package function is deprecated:i",
      "-Wconf:msg=class Field4 in package function is deprecated:i",
      "-Wconf:msg=class Field5 in package function is deprecated:i",
      "-Wconf:msg=class Field6 in package function is deprecated:i",
      "-Wconf:msg=class Possible in package function is deprecated:i",
      "-Wconf:msg=class Reverse in package function is deprecated:i",
      "-Wconf:msg=class Snoc in package function is deprecated:i",
      "-Wconf:msg=class Snoc1 in package function is deprecated:i",
    )
  },
  libraryDependencies ++= {
    if (isDotty.value) Seq.empty
    else
      Seq(
        compilerPlugin(kindProjector)
      )
  },
  scmInfo := Some(
    ScmInfo(url("https://github.com/optics-dev/Monocle"), "scm:git:git@github.com:optics-dev/Monocle.git")
  ),
  useScala3doc := false,
  testFrameworks += new TestFramework("munit.Framework"),
  Compile / doc / sources := { if (isDotty.value) Seq() else (Compile / doc / sources).value }
)

lazy val catsVersion  = "2.4.2"
lazy val dottyVersions = Seq("3.0.0-M3")

lazy val cats              = Def.setting("org.typelevel" %%% "cats-core" % catsVersion)
lazy val catsFree          = Def.setting("org.typelevel" %%% "cats-free" % catsVersion)
lazy val catsLaws          = Def.setting("org.typelevel" %%% "cats-laws" % catsVersion)
lazy val alleycats         = Def.setting("org.typelevel" %%% "alleycats-core" % catsVersion)
lazy val shapeless         = Def.setting("com.chuusai" %%% "shapeless" % "2.3.3")
lazy val refinedDep        = Def.setting("eu.timepit" %%% "refined" % "0.9.21")
lazy val refinedScalacheck = Def.setting("eu.timepit" %%% "refined-scalacheck" % "0.9.21" % "test")

lazy val discipline      = Def.setting("org.typelevel" %%% "discipline-core" % "1.1.4")
lazy val munit           = Def.setting("org.scalameta" %% "munit" % "0.7.21" % Test)
lazy val munitDiscipline = Def.setting("org.typelevel" %% "discipline-munit" % "1.0.6" % Test)

lazy val macroVersion = "2.1.1"

def mimaSettings(module: String): Seq[Setting[_]] = Seq(
  mimaPreviousArtifacts := Set("com.github.julien-truffaut" %% s"monocle-${module}" % "2.0.0")
)

lazy val gitRev = sys.process.Process("git rev-parse HEAD").lineStream_!.head

lazy val scalajsSettings = Seq(
  scalacOptions ++= {
    if (isDotty.value)
      Seq.empty
    else {
      val tag = (version in ThisBuild).value
      val s        = if (isSnapshot.value) gitRev else tag
      val a        = (baseDirectory in LocalRootProject).value.toURI.toString
      val g        = "https://raw.githubusercontent.com/optics-dev/Monocle"
      Seq(s"-P:scalajs:mapSourceURI:$a->$g/$s/")
    }
  },
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-maxSize", "8", "-minSuccessfulTests", "50")
)

// copied from cats build
def scalaVersionSpecificFolders(srcName: String, srcBaseDir: java.io.File, scalaVersion: String) = {
  def extraDirs(suffix: String) =
    List(CrossType.Pure, CrossType.Full)
      .flatMap(_.sharedSrcDir(srcBaseDir, srcName).toList.map(f => file(f.getPath + suffix)))

  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, y))     => extraDirs("-2.x") ++ (if (y >= 13) extraDirs("-2.13+") else Nil)
    case Some((0 | 3, _)) => extraDirs("-2.13+") ++ extraDirs("-3.x")
    case _                => Nil
  }
}

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
    _.jsSettings(monocleJsSettings)
  )
  .jvmSettings(mimaSettings("core"): _*)
  .settings(libraryDependencies ++= Seq(cats.value, catsFree.value))
  .settings(
    crossScalaVersions ++= dottyVersions,
    moduleName := "monocle-core",
    scalacOptions ~= (_.filterNot(
      Set(
        "-Xfatal-warnings", // Workaround for sbt bug
        "-source:3.0-migration",
      )
    )),
    libraryDependencies ++= Seq(
      munitDiscipline.value,
    )
  )

lazy val generic = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core, law % "test->test")
  .settings(moduleName := "monocle-generic")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .jvmSettings(mimaSettings("generic"): _*)
  .settings(libraryDependencies ++= Seq(cats.value, shapeless.value, munitDiscipline.value))

lazy val refined = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core, law)
  .settings(moduleName := "monocle-refined")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(
    crossScalaVersions ++= dottyVersions,
    libraryDependencies ++= Seq(
      cats.value,
      refinedDep.value,
      munitDiscipline.value,
      refinedScalacheck.value
    )
  )

lazy val law = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core)
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(
    moduleName := "monocle-law",
    crossScalaVersions ++= dottyVersions
  )
  .settings(libraryDependencies += discipline.value)

lazy val macros = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core, law % "test->test")
  .in(file("macro"))
  .settings(moduleName := "monocle-macro")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(
    crossScalaVersions ++= dottyVersions,
    scalacOptions += "-language:experimental.macros",
    libraryDependencies ++= {
      Seq(munitDiscipline.value) ++
      {if (isDotty.value) Seq.empty else Seq(
        scalaOrganization.value % "scala-reflect" % scalaVersion.value,
        scalaOrganization.value % "scala-compiler" % scalaVersion.value % "provided"
      )}
    }
  )

lazy val state = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core)
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(
    moduleName := "monocle-state",
    crossScalaVersions ++= dottyVersions
  )
  .settings(libraryDependencies ++= Seq(cats.value))

lazy val unsafe = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core)
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(
    moduleName := "monocle-unsafe",
    crossScalaVersions ++= dottyVersions
  )
  .jvmSettings(mimaSettings("unsafe"): _*)
  .settings(libraryDependencies ++= Seq(cats.value, alleycats.value))

lazy val test = crossProject(JVMPlatform, JSPlatform).dependsOn(core, law, state, unsafe)
  .settings(moduleName := "monocle-test")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(noPublishSettings: _*)
  .settings(
    crossScalaVersions ++= dottyVersions,
    libraryDependencies ++= Seq(
      cats.value,
      catsLaws.value,
      munitDiscipline.value,
    )
  )

lazy val bench = project.dependsOn(core.jvm, generic.jvm, macros.jvm)
  .settings(moduleName := "monocle-bench")
  .settings(monocleJvmSettings)
  .settings(noPublishSettings)
  .enablePlugins(JmhPlugin)

lazy val example = project.dependsOn(core.jvm, generic.jvm, refined.jvm, macros.jvm, state.jvm, test.jvm % "test->test")
  .settings(moduleName := "monocle-example")
  .settings(monocleJvmSettings)
  .settings(noPublishSettings)
  .settings(
    libraryDependencies ++= Seq(cats.value, shapeless.value, munitDiscipline.value)
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
  scalacOptions ~= (_.filterNot(_.startsWith("-Wconf"))),
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
  val stable   = (isVersionStable in ThisBuild).value

  if (!snapshot && stable) {
    (version in ThisBuild).value
  } else {
    (previousStableVersion in ThisBuild).value.getOrElse("0.0.0")
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
