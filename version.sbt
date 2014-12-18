
import org.typelevel.sbt.ReleaseSeries
import org.typelevel.sbt.Version._

TypelevelKeys.series in ThisBuild := ReleaseSeries(1,1)

TypelevelKeys.relativeVersion in ThisBuild := Relative(0,Snapshot)
