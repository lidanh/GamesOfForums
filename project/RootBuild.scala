import play.PlayImport._
import play.PlayScala
import sbt.Keys._
import sbt._

object RootBuild extends Build {
  val appName = "games-of-forums"
  val appVersion = "1.0-SNAPSHOT"

  lazy val testsSettings = Seq(
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "3.3.1" % Test,
      "org.specs2" %% "specs2-junit" % "3.3.1" % Test,
      "org.specs2" %% "specs2-html" % "3.3.1" % Test)
  )

  lazy val commonSettings = Seq(
    name := appName,
    version := appVersion,
    scalaVersion := "2.11.6",
    // resolvers
    resolvers ++= Seq(
      "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"),
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

  lazy val coreModule = (project in file("core")).
    dependsOn(shingimmel % "test->test;compile->compile").
    settings(commonSettings: _*).
    settings(testsSettings: _*).
    settings(
      name := s"$appName-core",

      libraryDependencies ++= Seq(
        "com.twitter" % "util-core_2.10" % "6.23.0",
        "com.wix" %% "accord-core" % "0.5-SNAPSHOT",
        "com.sendgrid" % "sendgrid-java" % "2.0.0",
        "com.typesafe" % "config" % "1.2.1",
        "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
        "ch.qos.logback" % "logback-classic" % "1.1.3",
        "com.typesafe.slick" %% "slick" % "3.0.0-RC2",
        "com.h2database" % "h2" % "1.3.175",

        // test packages
        "org.specs2" %% "specs2-mock" % "3.3.1" % Test,
        "com.wix" %% "accord-specs2" % "0.5-SNAPSHOT" % Test intransitive())
    )

  lazy val webModule = (project in file("web")).
    enablePlugins(PlayScala).
    dependsOn(coreModule).
    settings(commonSettings: _*).
    settings(testsSettings: _*).
    settings(
      name := s"$appName-web",

      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play-test" % "2.3.8" % Test excludeAll(
          ExclusionRule(organization = "org.specs2")
          ),
        jdbc,
        anorm,
        cache,
        ws
        )
    )

}