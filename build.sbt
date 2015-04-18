import sbt.Keys._

name := "GamesOfForums"

version := "1.0"

scalaVersion := "2.11.6"

// resolvers
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

// Read here for optional jars and dependencies
libraryDependencies ++= Seq(
"com.twitter" % "util-core_2.10" % "6.23.0",
"com.wix" %% "accord-core" % "0.5-SNAPSHOT",

// test packages
"org.specs2" %% "specs2-core" % "3.3.1" % Test,
"org.specs2" %% "specs2-mock" % "3.3.1" % Test,
"com.wix" %% "accord-specs2" % "0.5-SNAPSHOT" % Test intransitive())

scalacOptions ++= Seq("-feature", "-language:implicitConversions")

scalacOptions in Test ++= Seq("-Yrangepos")
    