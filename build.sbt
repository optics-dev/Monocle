import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import com.typesafe.sbt.SbtSite.SiteKeys._
import com.typesafe.tools.mima.core.MissingMethodProblem
import com.typesafe.tools.mima.core.ProblemFilters.exclude
import com.typesafe.tools.mima.plugin.MimaKeys.{binaryIssueFilters, previousArtifact}
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross.CrossProject
import sbt.Keys._
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
import sbtunidoc.Plugin.UnidocKeys._

lazy val buildSettings = Seq(
  organization       := "com.github.julien-truffaut",
  scalaVersion       := "2.11.8",
  crossScalaVersions := Seq("2.10.6", "2.11.8"),
  scalacOptions     ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:implicitConversions", "-language:higherKinds", "-language:postfixOps",
    "-unchecked",
    "-Xfatal-warnings",
    "-Yinline-warnings",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-value-discard",
    "-Xfuture"
  ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2,10)) => Seq("-Yno-generic-signatures") // no generic signatures for scala 2.10.x, see SI-7932, #571 and #828
    case _ => Seq( // https://github.com/scala/make-release-notes/blob/9cfbdc8c92f94/experimental-backend.md#emitting-java-8-style-lambdas
      "-Ybackend:GenBCode",
      "-Ydelambdafy:method",
      "-target:jvm-1.8"
    )
  }),
  resolvers ++= Seq(
    "bintray/non" at "http://dl.bintray.com/non/maven",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  scmInfo := Some(ScmInfo(url("https://github.com/julien-truffaut/Monocle"), "scm:git:git@github.com:julien-truffaut/Monocle.git"))
)

lazy val scalaz     = Def.setting("org.scalaz"      %%% "scalaz-core" % "7.2.2")
lazy val shapeless  = Def.setting("com.chuusai"     %%% "shapeless"   % "2.3.0")

lazy val refinedVersion = "0.4.0"
lazy val refinedDep = Def.setting("eu.timepit"      %%% "refined"     % refinedVersion)
lazy val refinedScalaCheckDep = Def.setting("eu.timepit"  %%% "refined-scalacheck" % refinedVersion % "test")

lazy val discpline  = Def.setting("org.typelevel"   %%% "discipline"  % "0.4")
lazy val scalatest  = Def.setting("org.scalatest"   %%% "scalatest"   % "3.0.0-M7"  % "test")

lazy val macroCompat = Def.setting("org.typelevel" %%% "macro-compat" % "1.1.0")

lazy val macroVersion = "2.1.0"
lazy val paradisePlugin = compilerPlugin("org.scalamacros" %  "paradise"       % macroVersion cross CrossVersion.full)

lazy val kindProjector = "org.spire-math" % "kind-projector" % "0.7.1" cross CrossVersion.binary

def mimaSettings(module: String): Seq[Setting[_]] = mimaDefaultSettings ++ Seq(
  previousArtifact := Some("com.github.julien-truffaut" %  (s"monocle-${module}_2.11") % "1.2.0"),
  binaryIssueFilters ++= Seq(
    exclude[MissingMethodProblem]("monocle.std.DoubleOptics.monocle$std$DoubleOptics$_setter_$doubleToFloat_="),
    exclude[MissingMethodProblem]("monocle.std.DoubleOptics.doubleToFloat"),
    exclude[MissingMethodProblem]("monocle.function.AtFunctions.remove"),
    exclude[MissingMethodProblem]("monocle.generic.ProductOptics.hNilEach"),
    exclude[MissingMethodProblem]("monocle.generic.ProductOptics.hConsEach"),
    exclude[MissingMethodProblem]("monocle.generic.ProductOptics.productEach")
  )
)

lazy val tagName = Def.setting(
 s"v${if (releaseUseGlobalVersion.value) (version in ThisBuild).value else version.value}")

lazy val gitRev =
  sys.process.Process("git rev-parse HEAD").lines_!.head

lazy val scalajsSettings = Seq(
  scalacOptions += {
    val s = if (isSnapshot.value) gitRev else tagName.value
    val a = (baseDirectory in LocalRootProject).value.toURI.toString
    val g = "https://raw.githubusercontent.com/julien-truffaut/Monocle"
    s"-P:scalajs:mapSourceURI:$a->$g/$s/"
  },
  scalaJSUseRhino := false,
  requiresDOM := false,
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck,
                           "-maxSize", "8",
                           "-minSuccessfulTests", "50")
)

lazy val monocleSettings    = buildSettings ++ publishSettings
lazy val monocleJvmSettings = monocleSettings
lazy val monocleJsSettings  = monocleSettings ++ scalajsSettings

lazy val monocleCrossSettings = (_: CrossProject)
  .jvmSettings(monocleJvmSettings: _*)
  .jsSettings(monocleJsSettings: _*)

lazy val monocle = project.in(file("."))
  .settings(moduleName := "monocle")
  .settings(monocleSettings)
  .aggregate(monocleJVM, monocleJS)
  .dependsOn(monocleJVM, monocleJS)

lazy val monocleJVM = project.in(file(".monocleJVM"))
  .settings(monocleJvmSettings)
  .aggregate(
    coreJVM, genericJVM, lawJVM, macrosJVM, stateJVM, refinedJVM, testJVM,
    example, docs, bench)
  .dependsOn(
    coreJVM, genericJVM, lawJVM, macrosJVM, stateJVM, refinedJVM, testJVM % "test-internal -> test",
    bench % "compile-internal;test-internal -> test")

lazy val monocleJS = project.in(file(".monocleJS"))
  .settings(monocleJsSettings)
  .aggregate(coreJS, genericJS, lawJS, macrosJS, stateJS, refinedJS, testJS)
  .dependsOn(coreJS, genericJS, lawJS, macrosJS, stateJS, refinedJS, testJS  % "test-internal -> test")

lazy val coreJVM = core.jvm
lazy val coreJS  = core.js
lazy val core    = crossProject
  .settings(moduleName := "monocle-core")
  .configure(monocleCrossSettings)
  .jvmSettings(mimaSettings("core"): _*)
  .settings(libraryDependencies += scalaz.value)
  .settings(addCompilerPlugin(kindProjector))
  .jvmSettings(
    libraryDependencies ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
      case Some((2, 11)) => "org.scala-lang.modules" %% "scala-java8-compat" % "0.7.0"
    }.toList
  )

lazy val genericJVM = generic.jvm
lazy val genericJS  = generic.js
lazy val generic    = crossProject.dependsOn(core)
  .settings(moduleName := "monocle-generic")
  .configure(monocleCrossSettings)
  .jvmSettings(mimaSettings("generic"): _*)
  .settings(libraryDependencies ++= Seq(scalaz.value, shapeless.value))

lazy val refinedJVM = refined.jvm
lazy val refinedJS  = refined.js
lazy val refined    = crossProject.dependsOn(core)
  .settings(moduleName := "monocle-refined")
  .configure(monocleCrossSettings)
  .settings(libraryDependencies ++= Seq(scalaz.value, refinedDep.value))

lazy val lawJVM = law.jvm
lazy val lawJS  = law.js
lazy val law    = crossProject.dependsOn(core)
  .settings(moduleName := "monocle-law")
  .configure(monocleCrossSettings)
  .settings(libraryDependencies ++= Seq(discpline.value))

lazy val macrosJVM = macros.jvm
lazy val macrosJS  = macros.js
lazy val macros    = crossProject.dependsOn(core)
  .in(file("macro"))
  .settings(moduleName := "monocle-macro")
  .configure(monocleCrossSettings)
  .settings(
    scalacOptions += "-language:experimental.macros",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect"  % scalaVersion.value,
      "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided",
      macroCompat.value
    ),
    addCompilerPlugin(paradisePlugin),
    libraryDependencies ++= CrossVersion partialVersion scalaVersion.value collect {
        case (2, scalaMajor) if scalaMajor < 11 => Seq("org.scalamacros" %% "quasiquotes" % macroVersion)
      } getOrElse Nil,
    unmanagedSourceDirectories in Compile += (sourceDirectory in Compile).value / s"scala-${scalaBinaryVersion.value}"
  )

lazy val stateJVM = state.jvm
lazy val stateJS  = state.js
lazy val state    = crossProject.dependsOn(core)
  .settings(moduleName := "monocle-state")
  .configure(monocleCrossSettings)
  .settings(libraryDependencies ++= Seq(scalaz.value))

lazy val testJVM = test.jvm
lazy val testJS  = test.js
lazy val test    = crossProject.dependsOn(core, generic, macros, law, state, refined)
  .settings(moduleName := "monocle-test")
  .configure(monocleCrossSettings)
  .settings(noPublishSettings: _*)
  .settings(
    libraryDependencies ++= Seq(scalaz.value, shapeless.value, scalatest.value, refinedScalaCheckDep.value, compilerPlugin(paradisePlugin))
  )

lazy val bench = project.dependsOn(coreJVM, genericJVM, macrosJVM)
  .settings(moduleName := "monocle-bench")
  .settings(monocleJvmSettings)
  .settings(noPublishSettings)
  .settings(libraryDependencies ++= Seq(
    shapeless.value,
    compilerPlugin(paradisePlugin)
  )).enablePlugins(JmhPlugin)

lazy val example = project.dependsOn(coreJVM, genericJVM, refinedJVM, macrosJVM, stateJVM, testJVM % "test->test")
  .settings(moduleName := "monocle-example")
  .settings(monocleJvmSettings)
  .settings(noPublishSettings)
  .settings(
    libraryDependencies ++= Seq(scalaz.value, shapeless.value, scalatest.value, compilerPlugin(paradisePlugin))
  )

lazy val docs = project.dependsOn(coreJVM, example)
  .settings(moduleName := "monocle-docs")
  .settings(monocleSettings)
  .settings(noPublishSettings)
  .settings(unidocSettings)
  .settings(site.settings)
  .settings(ghpages.settings)
  .settings(docSettings)
  .settings(tutSettings)
  .settings(
    libraryDependencies ++= Seq(scalaz.value, shapeless.value, compilerPlugin(paradisePlugin))
  )


lazy val docSettings = Seq(
  autoAPIMappings := true,
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(coreJVM),
  site.addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), "api"),
  site.addMappingsToSiteDir(tut, "_tut"),
  ghpagesNoJekyll := false,
  scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
    "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath
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
  publishTo <<= version { (v: String) =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
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
    </developers>
    ),
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

addCommandAlias("validate", ";compile;test;unidoc;tut")

// For Travis CI - see http://www.cakesolutions.net/teamblogs/publishing-artefacts-to-oss-sonatype-nexus-using-sbt-and-travis-ci
credentials ++= (for {
  username <- Option(System.getenv().get("SONATYPE_USERNAME"))
  password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
} yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq
