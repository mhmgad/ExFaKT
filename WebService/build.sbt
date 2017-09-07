scalaVersion := Option(System.getProperty("scala.version")).getOrElse("2.12.2")

lazy val main = (project in file("."))
  .enablePlugins(SbtWeb)
  .settings(
    libraryDependencies ++= Seq(
      "org.webjars" % "bootstrap" % "3.3.7",
      "org.webjars" % "flot" % "0.8.3"
    ),
    sourceDirectory in Assets := baseDirectory.value / "app/assets",
    resourceDirectory in Assets := baseDirectory.value / "public",
    target := baseDirectory.value / "target/sbt"
  )