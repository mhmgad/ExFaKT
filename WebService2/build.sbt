name := """webservice2"""
organization := "factchecking"
version := "1.0-SNAPSHOT"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava).settings(
  libraryDependencies ++= Seq(
    "org.webjars" % "bootstrap" % "3.3.7",
    "org.webjars" % "flot" % "0.8.3",
    "org.webjars" % "font-awesome" % "4.7.0"
  ))

scalaVersion := "2.12.7"

resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository"
//unmanagedBase := baseDirectory.value / "libs"

libraryDependencies += guice



libraryDependencies += "factchecking" % "api" % "1.0"
libraryDependencies += "de.mpii.exfakt" % "utils" % "1.0-SNAPSHOT"
libraryDependencies += "factchecking" % "query-rewriting" % "1.0"

libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.11.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.11.1"