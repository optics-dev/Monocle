import com.typesafe.tools.mima.core._
import sbt.Keys._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  List(
    organization := "com.github.julien-truffaut",
    homepage := Some(url("https://github.com/optics-dev/Monocle")),
    licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
    developers :=
      List(
        "aoiroaoino"      -> "Naoki Aoyama",
        "cquiroz"         -> "Carlos Quiroz",
        "kenbot"          -> "Ken Scambler",
        "julien-truffaut" -> "Julien Truffaut",
        "NightRa"         -> "Ilan Godik",
        "xuwei-k"         -> "Kenji Yoshida",
        "yilinwei"        -> "Yilin Wei"
      ).map { case (username, fullName) =>
        Developer(username, fullName, s"@$username", url(s"https://github.com/$username"))
      }
  )
)

lazy val kindProjector = "org.typelevel" % "kind-projector" % "0.11.3" cross CrossVersion.full

lazy val buildSettings = Seq(
  scalaVersion := "2.13.5",
  crossScalaVersions := Seq("2.13.5"),
  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  Compile / unmanagedSourceDirectories ++= scalaVersionSpecificFolders("main", baseDirectory.value, scalaVersion.value),
  Test / unmanagedSourceDirectories ++= scalaVersionSpecificFolders("test", baseDirectory.value, scalaVersion.value),
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-feature",
    "-unchecked",
    "-deprecation"
  ) ++ { if (isDotty.value) Seq() else Seq("-Xfatal-warnings") }, // Scala 3 doesn't support -Wconf
  Compile / console / scalacOptions -= "-Ywarn-unused:imports",
  scalacOptions ++= {
    if (isDotty.value)
      Seq("-source:3.0-migration", "-Ykind-projector", "-language:implicitConversions,higherKinds,postfixOps")
    else
      Seq(
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
        "-Wconf:msg=class Snoc1 in package function is deprecated:i"
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
  testFrameworks += new TestFramework("munit.Framework"),
  Compile / doc / scalacOptions ++= {
    if (!isDotty.value) Nil
    else Seq("-source-links:github://optics-dev/Monocle", "-revision", revisionToUse.value)
  }
)

lazy val catsVersion   = "2.4.2"
lazy val dottyVersions = Seq("3.0.0-RC1")

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

def revisionToUse = Def.task {
  val tag = (ThisBuild / version).value
  if (isSnapshot.value) gitRev else tag
}

lazy val scalajsSettings = Seq(
  scalacOptions ++= {
    if (isDotty.value)
      Seq.empty
    else {
      val s = revisionToUse.value
      val a = (LocalRootProject / baseDirectory).value.toURI.toString
      val g = "https://raw.githubusercontent.com/optics-dev/Monocle"
      Seq(s"-P:scalajs:mapSourceURI:$a->$g/$s/")
    }
  },
  Test / testOptions += Tests.Argument(TestFrameworks.ScalaCheck, "-maxSize", "8", "-minSuccessfulTests", "50")
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

lazy val monocle = project
  .in(file("."))
  .settings(moduleName := "monocle")
  .settings(noPublishSettings)
  .settings(monocleSettings)
  .aggregate(monocleJVM, monocleJS)
  .dependsOn(monocleJVM, monocleJS)

lazy val monocleJVM = project
  .in(file(".monocleJVM"))
  .settings(monocleJvmSettings)
  .settings(noPublishSettings)
  .aggregate(core.jvm, generic.jvm, law.jvm, macros.jvm, state.jvm, refined.jvm, unsafe.jvm, test.jvm, example, bench)
  .dependsOn(
    core.jvm,
    generic.jvm,
    law.jvm,
    macros.jvm,
    state.jvm,
    refined.jvm,
    unsafe.jvm,
    test.jvm % "test-internal -> test",
    bench    % "compile-internal;test-internal -> test"
  )

lazy val monocleJS = project
  .in(file(".monocleJS"))
  .settings(monocleJsSettings)
  .settings(noPublishSettings)
  .aggregate(core.js, generic.js, law.js, macros.js, state.js, refined.js, unsafe.js, test.js)
  .dependsOn(core.js, generic.js, law.js, macros.js, state.js, refined.js, unsafe.js, test.js % "test-internal -> test")

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
        "-source:3.0-migration"
      )
    )),
    libraryDependencies ++= Seq(
      munitDiscipline.value
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
      Seq(munitDiscipline.value) ++ {
        if (isDotty.value) Seq.empty
        else
          Seq(
            scalaOrganization.value % "scala-reflect"  % scalaVersion.value,
            scalaOrganization.value % "scala-compiler" % scalaVersion.value % "provided"
          )
      }
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

lazy val test = crossProject(JVMPlatform, JSPlatform)
  .dependsOn(core, law, state, unsafe, macros)
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
      munitDiscipline.value
    )
  )

lazy val bench = project
  .dependsOn(core.jvm, generic.jvm, macros.jvm)
  .settings(moduleName := "monocle-bench")
  .settings(monocleJvmSettings)
  .settings(noPublishSettings)
  .enablePlugins(JmhPlugin)

lazy val example = project
  .dependsOn(core.jvm, generic.jvm, refined.jvm, macros.jvm, state.jvm, test.jvm % "test->test")
  .settings(moduleName := "monocle-example")
  .settings(monocleJvmSettings)
  .settings(noPublishSettings)
  .settings(
    libraryDependencies ++= Seq(cats.value, shapeless.value, munitDiscipline.value)
  )

lazy val docs = project
  .dependsOn(core.jvm, unsafe.jvm, macros.jvm, example)
  .enablePlugins(BuildInfoPlugin, DocusaurusPlugin, MdocPlugin, ScalaUnidocPlugin)
  .settings(moduleName := "monocle-docs")
  .settings(monocleSettings)
  .settings(noPublishSettings)
  .settings(mdocSettings)
  .settings(buildInfoSettings)
  .settings(scalacOptions ~= (_.filterNot(Set("-Ywarn-unused:imports", "-Ywarn-dead-code"))))
  .settings(
    libraryDependencies ++= Seq(cats.value, shapeless.value)
  )

lazy val buildInfoSettings = Seq(
  buildInfoPackage := "monocle.build",
  buildInfoObject := "info",
  buildInfoKeys := Seq[BuildInfoKey](
    scalaVersion,
    scalacOptions,
    sourceDirectory,
    ThisBuild / latestVersion,
    BuildInfoKey.map(ThisBuild / version) { case (_, v) =>
      "latestSnapshotVersion" -> v
    },
    BuildInfoKey.map(core.jvm / moduleName) { case (k, v) =>
      "core" ++ k.capitalize -> v
    },
    BuildInfoKey.map(core.jvm / crossScalaVersions) { case (k, v) =>
      "core" ++ k.capitalize -> v
    },
    LocalRootProject / organization,
    core.jvm / crossScalaVersions
  )
)

lazy val mdocSettings = Seq(
  mdoc := (Compile / run).evaluated,
  scalacOptions --= Seq("-Xfatal-warnings", "-Ywarn-unused"),
  scalacOptions ~= (_.filterNot(_.startsWith("-Wconf"))),
  crossScalaVersions := Seq(scalaVersion.value),
  ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(core.jvm),
  (ScalaUnidoc / unidoc / target) := (LocalRootProject / baseDirectory).value / "website" / "static" / "api",
  cleanFiles += (ScalaUnidoc / unidoc / target).value,
  docusaurusCreateSite := docusaurusCreateSite
    .dependsOn(Compile / unidoc)
    .dependsOn(ThisBuild / updateSiteVariables)
    .value,
  docusaurusPublishGhpages :=
    docusaurusPublishGhpages
      .dependsOn(Compile / unidoc)
      .dependsOn(ThisBuild / updateSiteVariables)
      .value,
  (ScalaUnidoc / unidoc / scalacOptions) ++= Seq(
    "-doc-source-url",
    s"https://github.com/optics-dev/Monocle/tree/v${(ThisBuild / latestVersion).value}€{FILE_PATH}.scala",
    "-sourcepath",
    (LocalRootProject / baseDirectory).value.getAbsolutePath,
    "-doc-title",
    "Monocle",
    "-doc-version",
    s"v${(ThisBuild / latestVersion).value}"
  )
)

def minorVersion(version: String): String = {
  val (major, minor) =
    CrossVersion.partialVersion(version).get
  s"$major.$minor"
}

val latestVersion = settingKey[String]("Latest stable released version")
ThisBuild / latestVersion := {
  val snapshot = (ThisBuild / isSnapshot).value
  val stable   = (ThisBuild / isVersionStable).value

  if (!snapshot && stable) {
    (ThisBuild / version).value
  } else {
    (ThisBuild / previousStableVersion).value.getOrElse("0.0.0")
  }
}

val updateSiteVariables = taskKey[Unit]("Update site variables")
ThisBuild / updateSiteVariables := {
  val file = (LocalRootProject / baseDirectory).value / "website" / "variables.js"

  val variables =
    Map[String, String](
      "organization"   -> (LocalRootProject / organization).value,
      "coreModuleName" -> (core.jvm / moduleName).value,
      "latestVersion"  -> (ThisBuild / latestVersion).value,
      "scalaPublishVersions" -> {
        val minorVersions = (core.jvm / crossScalaVersions).value.map(minorVersion)
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
  publish / skip := true
)
