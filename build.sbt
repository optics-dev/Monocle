Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  List(
    tlBaseVersion := "3.2",
    organization  := "dev.optics",
    homepage      := Some(url("https://github.com/optics-dev/Monocle")),
    licenses      := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
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
      },
    scalaVersion            := scala2Version,
    crossScalaVersions      := Seq(scala2Version, scala3Version),
    tlSonatypeUseLegacyHost := false,
    tlCiScalafmtCheck       := true,
    tlCiReleaseBranches     := Seq("master"),
    githubWorkflowBuild += WorkflowStep.Sbt(
      List("docs/mdoc"),
      name = Some("Run documentation"),
      cond = Some(s"matrix.scala == '${scala2Version}' && matrix.project == 'rootJVM'")
    ),
    githubWorkflowPublishPostamble += WorkflowStep.Sbt(
      List("docs/docusaurusPublishGhpages"),
      name = Some("Publish website"),
      env = Map("GIT_DEPLOY_KEY" -> "${{ secrets.GIT_DEPLOY_KEY }}")
    )
  )
)

lazy val kindProjector = "org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full

lazy val buildSettings = Seq(
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-feature",
    "-unchecked",
    "-deprecation"
  ) ++ { if (tlIsScala3.value) Seq() else Seq("-Xfatal-warnings") }, // Scala 3 doesn't support -Wconf
  Compile / console / scalacOptions -= "-Ywarn-unused:imports",
  scalacOptions ++= {
    if (tlIsScala3.value)
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
        "-Wconf:msg=class Snoc1 in package function is deprecated:i",
        "-Wconf:msg=method first in trait:i",
        "-Wconf:msg=method second in trait:i",
        "-Wconf:msg=method left in trait:i",
        "-Wconf:msg=method right in trait:i"
      )
  },
  libraryDependencies ++= {
    if (tlIsScala3.value) Seq.empty
    else
      Seq(
        compilerPlugin(kindProjector)
      )
  }
)

lazy val catsVersion   = "2.10.0"
lazy val scala2Version = "2.13.11"
lazy val scala3Version = "3.3.1"

lazy val cats              = Def.setting("org.typelevel" %%% "cats-core" % catsVersion)
lazy val catsFree          = Def.setting("org.typelevel" %%% "cats-free" % catsVersion)
lazy val catsLaws          = Def.setting("org.typelevel" %%% "cats-laws" % catsVersion)
lazy val alleycats         = Def.setting("org.typelevel" %%% "alleycats-core" % catsVersion)
lazy val shapeless         = Def.setting("com.chuusai" %%% "shapeless" % "2.3.10")
lazy val refinedDep        = Def.setting("eu.timepit" %%% "refined" % "0.11.0")
lazy val refinedScalacheck = Def.setting("eu.timepit" %%% "refined-scalacheck" % "0.11.0" % "test")

lazy val discipline      = Def.setting("org.typelevel" %%% "discipline-core" % "1.5.1")
lazy val munit           = Def.setting("org.scalameta" %%% "munit" % "1.0.0-M6" % Test)
lazy val munitDiscipline = Def.setting("org.typelevel" %%% "discipline-munit" % "2.0.0-M3" % Test)

lazy val macroVersion = "2.1.1"

lazy val scalajsSettings = Seq(
  Test / testOptions += Tests.Argument(TestFrameworks.ScalaCheck, "-maxSize", "8", "-minSuccessfulTests", "50")
)

lazy val scalaNativeSettings = Seq(
  tlVersionIntroduced := List("2.13", "3").map(_ -> "3.2.0").toMap
)

lazy val monocleSettings       = buildSettings
lazy val monocleJvmSettings    = monocleSettings
lazy val monocleJsSettings     = monocleSettings ++ scalajsSettings
lazy val monocleNativeSettings = monocleSettings ++ scalaNativeSettings

lazy val root = tlCrossRootProject.aggregate(
  core,
  generic,
  law,
  macros,
  state,
  refined,
  unsafe,
  test,
  example,
  bench
)

lazy val core = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .jvmSettings(monocleJvmSettings)
  .jsSettings(monocleJsSettings)
  .nativeSettings(monocleNativeSettings)
  .settings(libraryDependencies ++= Seq(cats.value, catsFree.value))
  .settings(
    moduleName := "monocle-core",
    scalacOptions ~= (_.filterNot(
      Set(
        "-Xfatal-warnings", // Workaround for sbt bug
        "-source:3.0-migration"
      )
    )),
    libraryDependencies ++= Seq(
      munitDiscipline.value
    ),
    mimaBinaryIssueFilters ++= {
      import com.typesafe.tools.mima.core._

      if (tlIsScala3.value)
        Seq( // package-private objects moved in #1197
          ProblemFilters.exclude[MissingClassProblem]("monocle.syntax.AsPrism"),
          ProblemFilters.exclude[MissingClassProblem]("monocle.syntax.AsPrism$"),
          ProblemFilters.exclude[MissingClassProblem]("monocle.syntax.AsPrismImpl"),
          ProblemFilters.exclude[MissingClassProblem]("monocle.syntax.AsPrismImpl$")
        )
      else Nil
    }
  )

lazy val generic = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core, law % "test->test")
  .settings(
    moduleName      := "monocle-generic",
    publish / skip  := tlIsScala3.value,
    publishArtifact := !tlIsScala3.value
  )
  .jvmSettings(monocleJvmSettings)
  .jsSettings(monocleJsSettings)
  .nativeSettings(monocleNativeSettings)
  .settings(libraryDependencies ++= {
    if (tlIsScala3.value) Nil else Seq(cats.value, shapeless.value, munitDiscipline.value)
  })

lazy val refined = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core, law)
  .settings(moduleName := "monocle-refined")
  .jvmSettings(monocleJvmSettings)
  .jsSettings(monocleJsSettings)
  .nativeSettings(monocleNativeSettings)
  .settings(
    libraryDependencies ++= Seq(
      cats.value,
      refinedDep.value,
      munitDiscipline.value,
      refinedScalacheck.value
    )
  )

lazy val law = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core)
  .jvmSettings(monocleJvmSettings)
  .jsSettings(monocleJsSettings)
  .nativeSettings(monocleNativeSettings)
  .settings(
    moduleName := "monocle-law"
  )
  .settings(libraryDependencies += discipline.value)

lazy val macros = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core, law % "test->test")
  .in(file("macro"))
  .settings(moduleName := "monocle-macro")
  .jvmSettings(monocleJvmSettings)
  .jsSettings(monocleJsSettings)
  .nativeSettings(monocleNativeSettings)
  .settings(
    scalacOptions += "-language:experimental.macros",
    libraryDependencies ++= {
      Seq(munitDiscipline.value) ++ {
        if (tlIsScala3.value) Seq.empty
        else
          Seq(
            scalaOrganization.value % "scala-reflect"  % scalaVersion.value,
            scalaOrganization.value % "scala-compiler" % scalaVersion.value % "provided"
          )
      }
    }
  )

lazy val state = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core)
  .jvmSettings(monocleJvmSettings)
  .jsSettings(monocleJsSettings)
  .nativeSettings(monocleNativeSettings)
  .settings(
    moduleName := "monocle-state"
  )
  .settings(libraryDependencies ++= Seq(cats.value))

lazy val unsafe = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core)
  .jvmSettings(monocleJvmSettings)
  .jsSettings(monocleJsSettings)
  .nativeSettings(monocleNativeSettings)
  .settings(
    moduleName := "monocle-unsafe"
  )
  .settings(libraryDependencies ++= Seq(cats.value, alleycats.value))

lazy val test = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .dependsOn(core, law, state, unsafe, macros)
  .settings(moduleName := "monocle-test")
  .jvmSettings(monocleJvmSettings)
  .jsSettings(monocleJsSettings)
  .nativeSettings(monocleNativeSettings)
  .enablePlugins(NoPublishPlugin)
  .settings(
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
  .enablePlugins(NoPublishPlugin)
  .enablePlugins(JmhPlugin)

lazy val example = project
  .dependsOn(core.jvm, generic.jvm, refined.jvm, macros.jvm, state.jvm, test.jvm % "test->test")
  .settings(moduleName := "monocle-example")
  .settings(monocleJvmSettings)
  .enablePlugins(NoPublishPlugin)
  .settings(
    libraryDependencies ++= { if (tlIsScala3.value) Nil else Seq(cats.value, shapeless.value, munitDiscipline.value) }
  )

lazy val docs = project
  .dependsOn(core.jvm, unsafe.jvm, macros.jvm, example)
  .enablePlugins(BuildInfoPlugin, DocusaurusPlugin, MdocPlugin, ScalaUnidocPlugin)
  .settings(moduleName := "monocle-docs")
  .settings(monocleSettings)
  .enablePlugins(NoPublishPlugin)
  .settings(mdocSettings)
  .settings(buildInfoSettings)
  .settings(scalacOptions ~= (_.filterNot(Set("-Ywarn-unused:imports", "-Ywarn-dead-code"))))
  .settings(
    libraryDependencies ++= Seq(cats.value, shapeless.value)
  )

lazy val buildInfoSettings = Seq(
  buildInfoPackage := "monocle.build",
  buildInfoObject  := "info",
  buildInfoKeys := Seq[BuildInfoKey](
    scalaVersion,
    scalacOptions,
    sourceDirectory,
    BuildInfoKey.map(ThisBuild / tlLatestVersion) { case (_, v) =>
      "latestVersion" -> v.getOrElse("0.0.0")
    },
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
  crossScalaVersions                         := Seq(scalaVersion.value),
  ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(core.jvm),
  (ScalaUnidoc / unidoc / target)            := (LocalRootProject / baseDirectory).value / "website" / "static" / "api",
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
    s"https://github.com/optics-dev/Monocle/tree/v${tlLatestVersion.value.getOrElse(version.value)}€{FILE_PATH}.scala",
    "-sourcepath",
    (LocalRootProject / baseDirectory).value.getAbsolutePath,
    "-doc-title",
    "Monocle",
    "-doc-version",
    s"v${tlLatestVersion.value.getOrElse(version.value)}"
  )
)

def minorVersion(version: String): String = {
  val (major, minor) =
    CrossVersion.partialVersion(version).get
  s"$major.$minor"
}

val updateSiteVariables = taskKey[Unit]("Update site variables")
ThisBuild / updateSiteVariables := {
  val file = (LocalRootProject / baseDirectory).value / "website" / "variables.js"

  val variables =
    Map[String, String](
      "organization"   -> (LocalRootProject / organization).value,
      "coreModuleName" -> (core.jvm / moduleName).value,
      "latestVersion"  -> (ThisBuild / tlLatestVersion).value.getOrElse((ThisBuild / version).value),
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
