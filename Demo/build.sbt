name := """fact-checking-demo"""

version := "1.0-SNAPSHOT"

//lazy val api= RootProject(file("../api"))
//externalPom()
lazy val root = (project in file(".")).enablePlugins(PlayJava)
//.dependsOn(api)

scalaVersion := "2.12.2"

resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

libraryDependencies += guice

libraryDependencies += "factchecking" % "api" % "1.0"
libraryDependencies += "factchecking" % "client" % "1.0"

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.194"

libraryDependencies += "org.webjars" % "bootstrap" % "4.0.0-beta-1"
libraryDependencies += "org.webjars" % "font-awesome" % "5.0.2"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
