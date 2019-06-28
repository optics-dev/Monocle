import com.typesafe.tools.mima.plugin.MimaKeys.mimaPreviousArtifacts
import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import sbt.Keys._
import sbtcrossproject.CrossPlugin.autoImport.crossProject

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

lazy val scalatestVersion = settingKey[String]("")

lazy val buildSettings = Seq(
  scalaVersion       := "2.13.0",
  crossScalaVersions := Seq("2.12.8", "2.13.0"),
  scalatestVersion   := "3.0.8",
  scalacOptions     ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:implicitConversions", "-language:higherKinds", "-language:postfixOps",
    "-unchecked",
    "-Ywarn-dead-code",
    "-Ywarn-value-discard",
    "-Ywarn-unused:imports",
  ),
  scalacOptions ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
    case Some((2, n)) if n <= 12 => Seq("-Xfuture")
  }.toList.flatten,
  scalacOptions ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
    case Some((2, n)) if n >= 13 => Seq("-Ymacro-annotations")
  }.toList.flatten,
  scalacOptions in (Compile, console) -= "-Ywarn-unused:imports",
  scalacOptions in (Test   , console) -= "-Ywarn-unused:imports",
  addCompilerPlugin(kindProjector),
  scmInfo := Some(ScmInfo(url("https://github.com/julien-truffaut/Monocle"), "scm:git:git@github.com:julien-truffaut/Monocle.git"))
)

lazy val scalaz             = Def.setting("org.scalaz"      %%% "scalaz-core"          % "7.2.28")
lazy val shapeless          = Def.setting("com.chuusai"     %%% "shapeless"            % "2.3.3")

lazy val refinedDep         = Def.setting("eu.timepit"      %%% "refined"              % "0.9.8")
lazy val refinedScalacheck  = Def.setting("eu.timepit"      %%% "refined-scalacheck"   % "0.9.8" % "test")

lazy val discipline         = Def.setting("org.typelevel"   %%% "discipline-scalatest" % "0.12.0-M3")
lazy val scalacheck         = Def.setting("org.scalacheck"  %%% "scalacheck"           % "1.14.0")
lazy val scalatest          = Def.setting("org.scalatest"   %%% "scalatest"            % scalatestVersion.value % "test")

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

lazy val kindProjector  = "org.typelevel"  % "kind-projector" % "0.10.3" cross CrossVersion.binary

def mimaSettings(module: String): Seq[Setting[_]] = mimaDefaultSettings ++ Seq(
  mimaPreviousArtifacts := Set("com.github.julien-truffaut" %%  (s"monocle-${module}") % "1.6.0")
)

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
  .aggregate(
    coreJVM, genericJVM, lawJVM, macrosJVM, stateJVM, refinedJVM, unsafeJVM, testJVM,
    example, docs, bench)
  .dependsOn(
    coreJVM, genericJVM, lawJVM, macrosJVM, stateJVM, refinedJVM, unsafeJVM, testJVM % "test-internal -> test",
    bench % "compile-internal;test-internal -> test")

lazy val monocleJS = project.in(file(".monocleJS"))
  .settings(monocleJsSettings)
  .aggregate(coreJS, genericJS, lawJS, macrosJS, stateJS, refinedJS, unsafeJS, testJS)
  .dependsOn(coreJS, genericJS, lawJS, macrosJS, stateJS, refinedJS, unsafeJS, testJS  % "test-internal -> test")

lazy val coreJVM    = core.jvm
lazy val coreJS     = core.js
lazy val core       = crossProject(JVMPlatform, JSPlatform)
  .settings(moduleName := "monocle-core")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings),
  )
  .jvmSettings(mimaSettings("core"): _*)
  .settings(libraryDependencies += scalaz.value)

lazy val genericJVM = generic.jvm
lazy val genericJS  = generic.js
lazy val generic    = crossProject(JVMPlatform, JSPlatform).dependsOn(core)
  .settings(moduleName := "monocle-generic")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .jvmSettings(mimaSettings("generic"): _*)
  .settings(libraryDependencies ++= Seq(scalaz.value, shapeless.value))

lazy val refinedJVM = refined.jvm
lazy val refinedJS  = refined.js
lazy val refined    = crossProject(JVMPlatform, JSPlatform).dependsOn(core)
  .settings(moduleName := "monocle-refined")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(libraryDependencies ++= Seq(scalaz.value, refinedDep.value))

lazy val lawJVM = law.jvm
lazy val lawJS  = law.js
lazy val law    = crossProject(JVMPlatform, JSPlatform).dependsOn(core)
  .settings(moduleName := "monocle-law")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(libraryDependencies ++= Seq(discipline.value, scalacheck.value))

lazy val macrosJVM = macros.jvm
lazy val macrosJS  = macros.js
lazy val macros    = crossProject(JVMPlatform, JSPlatform).dependsOn(core)
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

lazy val stateJVM    = state.jvm
lazy val stateJS     = state.js
lazy val state       = crossProject(JVMPlatform, JSPlatform).dependsOn(core)
  .settings(moduleName := "monocle-state")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings),
  )
  .settings(libraryDependencies ++= Seq(scalaz.value))

lazy val unsafeJVM = unsafe.jvm
lazy val unsafeJS  = unsafe.js
lazy val unsafe    = crossProject(JVMPlatform, JSPlatform).dependsOn(core)
  .settings(moduleName := "monocle-unsafe")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .jvmSettings(mimaSettings("unsafe"): _*)
  .settings(libraryDependencies ++= Seq(scalaz.value, shapeless.value))

lazy val testJVM = test.jvm
lazy val testJS  = test.js
lazy val test    = crossProject(JVMPlatform, JSPlatform).dependsOn(core, generic, macros, law, state, refined, unsafe)
  .settings(moduleName := "monocle-test")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(noPublishSettings: _*)
  .settings(
    libraryDependencies ++= Seq(scalaz.value, shapeless.value, scalatest.value, refinedScalacheck.value),
    libraryDependencies ++= paradisePlugin.value
  )

lazy val bench = project.dependsOn(coreJVM, genericJVM, macrosJVM)
  .settings(moduleName := "monocle-bench")
  .settings(monocleJvmSettings)
  .settings(noPublishSettings)
  .settings(
    libraryDependencies ++= Seq(shapeless.value),
    libraryDependencies ++= paradisePlugin.value
  ).enablePlugins(JmhPlugin)

lazy val example = project.dependsOn(coreJVM, genericJVM, refinedJVM, macrosJVM, stateJVM, testJVM % "test->test")
  .settings(moduleName := "monocle-example")
  .settings(monocleJvmSettings)
  .settings(noPublishSettings)
  .settings(
    libraryDependencies ++= Seq(scalaz.value, shapeless.value, scalatest.value),
    libraryDependencies ++= paradisePlugin.value
  )

lazy val docs = project.dependsOn(coreJVM, unsafeJVM, macrosJVM, example)
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(ScalaUnidocPlugin)
  .settings(moduleName := "monocle-docs")
  .settings(monocleSettings)
  .settings(noPublishSettings)
  .settings(docSettings)
  .settings(scalacOptions in Tut ~= (_.filterNot(Set("-Ywarn-unused:imports", "-Ywarn-dead-code"))))
  .settings(
    libraryDependencies ++= Seq(scalaz.value, shapeless.value),
    libraryDependencies ++= paradisePlugin.value
  )
  .enablePlugins(GhpagesPlugin)

lazy val docsMappingsAPIDir = settingKey[String]("Name of subdirectory in site target directory for api docs")

lazy val docSettings = Seq(
  micrositeName := "Monocle",
  micrositeDescription := "Optics library for Scala",
  micrositeHighlightTheme := "atom-one-light",
  micrositeHomepage := "http://julien-truffaut.github.io/Monocle",
  micrositeBaseUrl := "/Monocle",
  micrositeDocumentationUrl := "/Monocle/api",
  micrositeGithubOwner := "julien-truffaut",
  micrositeGithubRepo := "Monocle",
  micrositePalette := Map(
    "brand-primary"   -> "#5B5988",
    "brand-secondary" -> "#292E53",
    "brand-tertiary"  -> "#222749",
    "gray-dark"       -> "#49494B",
    "gray"            -> "#7B7B7E",
    "gray-light"      -> "#E5E5E6",
    "gray-lighter"    -> "#F4F3F4",
    "white-color"     -> "#FFFFFF"),
  autoAPIMappings := true,
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(coreJVM),
  docsMappingsAPIDir := "api",
  addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), docsMappingsAPIDir),
  ghpagesNoJekyll := false,
  fork in tut := true,
  fork in (ScalaUnidoc, unidoc) := true,
  scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
    "-Xfatal-warnings",
    "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath,
    "-diagrams"
  ),
  git.remoteRepo := "git@github.com:julien-truffaut/Monocle.git",
  includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.yml" | "*.md"
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  skip in publish := true
)

addCommandAlias("validate", ";compile;test;unidoc;tut")

// For Travis CI - see http://www.cakesolutions.net/teamblogs/publishing-artefacts-to-oss-sonatype-nexus-using-sbt-and-travis-ci
credentials ++= (for {
  username <- Option(System.getenv().get("SONATYPE_USERNAME"))
  password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
} yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq
