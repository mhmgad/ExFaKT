// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.4.1")

//addSbtPlugin("com.typesafe.sbt" % "sbt-js-engine" % "1.1.4")
//
//addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")
//
//addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.1")