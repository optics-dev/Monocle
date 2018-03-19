import com.typesafe.sbt.SbtSite.SiteKeys._
import com.typesafe.tools.mima.plugin.MimaKeys.mimaPreviousArtifacts
import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import sbtcrossproject.crossProject
import sbt.Keys._
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

lazy val Scala211 = "2.11.12"

lazy val buildSettings = Seq(
  organization       := "com.github.julien-truffaut",
  scalaVersion       := "2.12.4",
  crossScalaVersions := Seq(Scala211, "2.12.4"),
  scalacOptions     ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:implicitConversions", "-language:higherKinds", "-language:postfixOps",
    "-unchecked",
    "-Xfatal-warnings",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-value-discard",
    "-Xfuture"
  ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, n)) if n >= 11 =>
      Seq("-Ywarn-unused-import")
    case None =>
      Seq()
  }),
  scalacOptions in (Compile, console) -= "-Ywarn-unused-import",
  scalacOptions in (Test, console) -= "-Ywarn-unused-import",
  addCompilerPlugin(kindProjector),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  scmInfo := Some(ScmInfo(url("https://github.com/julien-truffaut/Monocle"), "scm:git:git@github.com:julien-truffaut/Monocle.git"))
)

lazy val catsVersion = "1.1.0"


lazy val cats              = Def.setting("org.typelevel"              %%% "cats-core"          % catsVersion)
lazy val catsFree          = Def.setting("org.typelevel"              %%% "cats-free"          % catsVersion)
lazy val catsLaws          = Def.setting("org.typelevel"              %%% "cats-laws"          % catsVersion)
lazy val alleycats         = Def.setting("org.typelevel"              %%% "alleycats-core"     % catsVersion)
lazy val newts             = Def.setting("com.github.julien-truffaut" %%% "newts-core"         % "0.3.1")
lazy val scalaz            = Def.setting("org.scalaz"                 %%% "scalaz-core"        % "7.2.18")
lazy val shapeless         = Def.setting("com.chuusai"                %%% "shapeless"          % "2.3.3")
lazy val refinedDep         = Def.setting("eu.timepit"      %%% "refined"              % "0.8.6")
lazy val refinedScalacheck  = Def.setting("eu.timepit"      %%% "refined-scalacheck"   % "0.8.6" % "test")

lazy val discipline        = Def.setting("org.typelevel"              %%% "discipline"         % "0.8")
lazy val scalacheck        = Def.setting("org.scalacheck"             %%% "scalacheck"         % "1.13.5")
lazy val scalatest         = Def.setting("org.scalatest"              %%% "scalatest"          % "3.0.4"  % "test")

lazy val macroCompat       = Def.setting("org.typelevel"              %%% "macro-compat"       % "1.1.1")

lazy val macroVersion = "2.1.1"
lazy val paradisePlugin = "org.scalamacros" % "paradise"       % macroVersion cross CrossVersion.patch
lazy val kindProjector  = "org.spire-math"  % "kind-projector" % "0.9.5" cross CrossVersion.binary

def mimaSettings(module: String): Seq[Setting[_]] = mimaDefaultSettings ++ Seq(
  mimaPreviousArtifacts := Set("com.github.julien-truffaut" %  (s"monocle-${module}_2.11") % "1.3.0")
)

lazy val tagName = Def.setting(
 s"v${if (releaseUseGlobalVersion.value) (version in ThisBuild).value else version.value}")

lazy val gitRev =
  sys.process.Process("git rev-parse HEAD").lineStream_!.head

lazy val scalajsSettings = Seq(
  scalacOptions += {
    lazy val tag = tagName.value
    val s = if (isSnapshot.value) gitRev else tag
    val a = (baseDirectory in LocalRootProject).value.toURI.toString
    val g = "https://raw.githubusercontent.com/julien-truffaut/Monocle"
    s"-P:scalajs:mapSourceURI:$a->$g/$s/"
  },
  jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck,
                           "-maxSize", "8",
                           "-minSuccessfulTests", "50")
)

lazy val monocleSettings    = buildSettings ++ publishSettings
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
    _.jsSettings(monocleJsSettings)
  )
  .jvmSettings(mimaSettings("core"): _*)
  .settings(libraryDependencies ++= Seq(cats.value, catsFree.value, newts.value))
  .jvmSettings(
    libraryDependencies ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
      case Some((2, 11)) => "org.scala-lang.modules" %% "scala-java8-compat" % "0.7.0"
    }.toList
  )

lazy val genericJVM = generic.jvm
lazy val genericJS  = generic.js
lazy val generic    = crossProject(JVMPlatform, JSPlatform).dependsOn(core)
  .settings(moduleName := "monocle-generic")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .jvmSettings(mimaSettings("generic"): _*)
  .settings(libraryDependencies ++= Seq(cats.value, shapeless.value))

lazy val refinedJVM = refined.jvm
lazy val refinedJS  = refined.js
lazy val refined    = crossProject(JVMPlatform, JSPlatform).dependsOn(core)
  .settings(moduleName := "monocle-refined")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(libraryDependencies ++= Seq(cats.value, refinedDep.value))

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
      macroCompat.value
    ),
    addCompilerPlugin(paradisePlugin),
    libraryDependencies ++= CrossVersion partialVersion scalaVersion.value collect {
        case (2, scalaMajor) if scalaMajor < 11 => Seq("org.scalamacros" %% "quasiquotes" % macroVersion)
      } getOrElse Nil,
    unmanagedSourceDirectories in Compile += (sourceDirectory in Compile).value / s"scala-${scalaBinaryVersion.value}"
  )

lazy val stateJVM    = state.jvm
lazy val stateJS     = state.js
lazy val state       = crossProject(JVMPlatform, JSPlatform).dependsOn(core)
  .settings(moduleName := "monocle-state")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .settings(libraryDependencies ++= Seq(cats.value))

lazy val unsafeJVM = unsafe.jvm
lazy val unsafeJS  = unsafe.js
lazy val unsafe    = crossProject(JVMPlatform, JSPlatform).dependsOn(core)
  .settings(moduleName := "monocle-unsafe")
  .configureCross(
    _.jvmSettings(monocleJvmSettings),
    _.jsSettings(monocleJsSettings)
  )
  .jvmSettings(mimaSettings("unsafe"): _*)
  .settings(libraryDependencies ++= Seq(cats.value, alleycats.value, shapeless.value))

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
    libraryDependencies ++= Seq(cats.value, catsLaws.value, shapeless.value, scalatest.value, refinedScalacheck.value, compilerPlugin(paradisePlugin))
  )

lazy val bench = project.dependsOn(coreJVM, genericJVM, macrosJVM)
  .settings(moduleName := "monocle-bench")
  .settings(monocleJvmSettings)
  .settings(noPublishSettings)
  .settings(libraryDependencies ++= Seq(
    scalaz.value,
    shapeless.value,
    compilerPlugin(paradisePlugin)
  )).enablePlugins(JmhPlugin)

lazy val example = project.dependsOn(coreJVM, genericJVM, refinedJVM, macrosJVM, stateJVM, testJVM % "test->test")
  .settings(moduleName := "monocle-example")
  .settings(monocleJvmSettings)
  .settings(noPublishSettings)
  .settings(
    libraryDependencies ++= Seq(cats.value, shapeless.value, scalatest.value, compilerPlugin(paradisePlugin))
  )

lazy val docs = project.dependsOn(coreJVM, unsafeJVM, macrosJVM, example)
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(ScalaUnidocPlugin)
  .settings(moduleName := "monocle-docs")
  .settings(monocleSettings)
  .settings(noPublishSettings)
  .settings(docSettings)
  .settings(scalacOptions in Tut ~= (_.filterNot(Set("-Ywarn-unused-import", "-Ywarn-dead-code"))))
  .settings(
    libraryDependencies ++= Seq(cats.value, shapeless.value, compilerPlugin(paradisePlugin))
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


lazy val publishSettings = Seq(
  homepage := Some(url("https://github.com/julien-truffaut/Monocle")),
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  autoAPIMappings := true,
  apiURL := Some(url("https://julien-truffaut.github.io/Monocle/api/")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  publishTo := version { (v: String) =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  }.value,
  pomExtra := (
    <developers>
      <developer>
        <id>julien-truffaut</id>
        <name>Julien Truffaut</name>
      </developer>
      <developer>
        <id>NightRa</id>
        <name>Ilan Godik</name>
      </developer>
      <developer>
        <id>aoiroaoino</id>
        <name>Naoki Aoyama</name>
      </developer>
      <developer>
        <id>xuwei-k</id>
        <name>Kenji Yoshida</name>
      </developer>
      <developer>
        <id>kenbot</id>
        <name>Ken Scambler</name>
      </developer>
    </developers>
    ),
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runTest,
    releaseStepCommand(s"++$Scala211"),
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    releaseStepCommand(s"++$Scala211"),
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
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
