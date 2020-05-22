import com.typesafe.tools.mima.plugin.MimaKeys.mimaPreviousArtifacts
import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import sbt.Keys._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val scalaJSVersion06 = Option(System.getenv("SCALAJS_VERSION")).exists(_.startsWith("0.6"))

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
  scalaVersion       := "2.13.1",
  crossScalaVersions := Seq("2.12.11", "2.13.1"),
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
lazy val scalaz            = Def.setting("org.scalaz"        %%% "scalaz-core"              % "7.3.1")
lazy val shapeless         = Def.setting("com.chuusai"       %%% "shapeless"                % "2.3.3")
lazy val refinedDep        = Def.setting("eu.timepit"        %%% "refined"                  % "0.9.14")
lazy val refinedScalacheck = Def.setting("eu.timepit"        %%% "refined-scalacheck"       % "0.9.14" % "test")

lazy val discipline           = Def.setting("org.typelevel"  %%% "discipline-core"          % "1.0.2")
lazy val discipline_scalatest = Def.setting("org.typelevel"  %%% "discipline-scalatest"     % "1.0.1")

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
lazy val monocleJvmSettings = monocleSettings ++ Seq(skip.in(publish) := scalaJSVersion06)
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
    example, docs, bench)
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
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(ScalaUnidocPlugin)
  .enablePlugins(MdocPlugin)
  .settings(moduleName := "monocle-docs")
  .settings(monocleSettings)
  .settings(noPublishSettings)
  .settings(docSettings)
  .settings(scalacOptions ~= (_.filterNot(Set("-Ywarn-unused:imports", "-Ywarn-dead-code"))))
  .settings(
    libraryDependencies ++= Seq(cats.value, shapeless.value),
    libraryDependencies ++= paradisePlugin.value
  )
  .enablePlugins(GhpagesPlugin)

lazy val docsMappingsAPIDir = settingKey[String]("Name of subdirectory in site target directory for api docs")

lazy val docSettings = Seq(
  micrositeName := "Monocle",
  micrositeDescription := "Optics library for Scala",
  micrositeHighlightTheme := "atom-one-light",
  micrositeHomepage := "https://www.optics.dev/Monocle/",
  micrositeBaseUrl := "/Monocle",
  micrositeDocumentationUrl := "/Monocle/api",
  micrositeGithubOwner := "julien-truffaut",
  micrositeGithubRepo := "Monocle",
  micrositePalette := Map(
    "brand-primary"   -> "#0085E6",
    "brand-secondary" -> "#004A87",
    "brand-tertiary"  -> "#004A87",
    "gray-dark"       -> "#49494B",
    "gray"            -> "#7B7B7E",
    "gray-light"      -> "#E5E5E6",
    "gray-lighter"    -> "#F4F3F4",
    "white-color"     -> "#FFFFFF"),
  autoAPIMappings := true,
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(core.jvm),
  docsMappingsAPIDir := "api",
  addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), docsMappingsAPIDir),
  ghpagesNoJekyll := false,
  micrositePushSiteWith := GitHub4s,
  micrositeGithubToken := Some(System.getenv().get("CI_TOKEN")),
  micrositeCompilingDocsTool := WithMdoc,
  mdocIn := (sourceDirectory in Compile).value / "mdoc",
  fork in mdoc := true,
  fork in (ScalaUnidoc, unidoc) := true,
  scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
    "-Xfatal-warnings",
    "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath,
    "-diagrams"
  ),
  git.remoteRepo := "git@github.com:optics-dev/Monocle.git",
  includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.yml" | "*.md"
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  skip in publish := true
)

addCommandAlias("validate", ";compile;test;unidoc;docs/mdoc")
addCommandAlias("ci-microsite", "docs/publishMicrosite")

// For Travis CI - see http://www.cakesolutions.net/teamblogs/publishing-artefacts-to-oss-sonatype-nexus-using-sbt-and-travis-ci
credentials ++= (for {
  username <- Option(System.getenv().get("SONATYPE_USERNAME"))
  password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
} yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq
