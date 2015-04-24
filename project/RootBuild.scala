import sbt.Tests.Argument
import sbt._
import sbt.Keys._

object RootBuild extends Build {
  lazy val testsSettings = Seq(
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "3.3.1" % Test,
      "org.specs2" %% "specs2-html" % "3.3.1" % Test)
  )

  lazy val commonSettings = Seq(
    name := "games-of-forums",
    version := "1.0",
    scalaVersion := "2.11.6",
    // resolvers
    resolvers ++= Seq(
      "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"),
    scalacOptions ++= Seq("-feature", "-language:implicitConversions"),
    scalacOptions in Test ++= Seq("-Yrangepos"),
    testOptions in Test += Tests.Argument("console")
  )

  lazy val shingimmel = (project in file("shin-gimmel")).
    settings(commonSettings: _*).
    settings(testsSettings: _*).
    settings(
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value
      ),

      name := "shin-gimmel"
    )

  lazy val apiModule = (project in file("api")).
    dependsOn(shingimmel % "test->test;compile->compile").
    settings(commonSettings: _*).
    settings(testsSettings: _*).
    settings(
      name := "api",

      libraryDependencies ++= Seq(
        "com.twitter" % "util-core_2.10" % "6.23.0",
        "com.wix" %% "accord-core" % "0.5-SNAPSHOT",

        // test packages
        "org.specs2" %% "specs2-mock" % "3.3.1" % Test,
        "com.wix" %% "accord-specs2" % "0.5-SNAPSHOT" % Test intransitive())
    )
}