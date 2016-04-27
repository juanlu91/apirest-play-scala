name := """apirest-play-scala"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.typesafe.play" %% "anorm" % "2.4.0",
  "org.webjars" % "jquery" % "2.1.4",
  "org.webjars" % "bootstrap" % "3.3.5"
)

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.38"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

fork in run := true
