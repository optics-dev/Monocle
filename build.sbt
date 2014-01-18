
name := "Lens"

version := "0.1"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
  "org.scalaz"        %%  "scalaz-core" % "7.0.5" ,
  "org.scalatest"     %%  "scalatest"   % "2.0.1-SNAP"   % "test",
  "org.scalacheck"    %% "scalacheck"   % "1.11.1"       % "test"
)
