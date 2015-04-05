name := "GamesOfForums"

version := "1.0"

scalaVersion := "2.11.6"

// Read here for optional jars and dependencies
libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.3.1" % "test",
  "com.twitter" % "util-core_2.10" % "6.23.0")

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

scalacOptions in Test ++= Seq("-Yrangepos")
    