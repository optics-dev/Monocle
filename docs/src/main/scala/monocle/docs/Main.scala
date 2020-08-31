package monocle.docs

import monocle.build.info._
import java.nio.file.{FileSystems, Path}
import scala.collection.Seq

object Main {
  def sourceDirectoryPath(rest: String*): Path =
    FileSystems.getDefault.getPath(sourceDirectory.getAbsolutePath, rest: _*)

  def minorVersion(version: String): String = {
    val Array(major, minor, _) = version.split('.')
    s"$major.$minor"
  }

  def majorVersion(version: String): String = {
    val Array(major, _, _) = version.split('.')
    major
  }

  def minorVersionsString(versions: Seq[String]): String = {
    val minorVersions = versions.map(minorVersion)
    if (minorVersions.size <= 2) minorVersions.mkString(" and ")
    else minorVersions.init.mkString(", ") ++ " and " ++ minorVersions.last
  }

  def main(args: Array[String]): Unit = {
    val scalaMinorVersion = minorVersion(scalaVersion)

    val settings = mdoc
      .MainSettings()
      .withSiteVariables {
        Map(
          "ORGANIZATION"              -> organization,
          "CORE_MODULE_NAME"          -> coreModuleName,
          "CORE_CROSS_SCALA_VERSIONS" -> minorVersionsString(coreCrossScalaVersions),
          "LATEST_VERSION"            -> latestVersion,
          "LATEST_SNAPSHOT_VERSION"   -> latestSnapshotVersion,
          "LATEST_MAJOR_VERSION"      -> majorVersion(latestVersion),
          "DOCS_SCALA_MINOR_VERSION"  -> scalaMinorVersion,
          "SCALA_PUBLISH_VERSIONS"    -> minorVersionsString(crossScalaVersions),
          "API_BASE_URL"              -> s"/monocle/api/monocle"
        )
      }
      .withScalacOptions(scalacOptions.mkString(" "))
      .withIn(sourceDirectoryPath("main", "mdoc"))
      .withArgs(args.toList)

    val exitCode = mdoc.Main.process(settings)
    if (exitCode != 0) sys.exit(exitCode)
  }
}
