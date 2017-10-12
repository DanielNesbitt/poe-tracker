name := "poe-tracker"

version := "0.1"

scalaVersion := "2.11.11"

libraryDependencies += "io.circe" %% "circe-parser" % "0.8.0"

libraryDependencies += "de.heikoseeberger" %% "akka-http-circe" % "1.15.0"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.0"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.6",
  "com.typesafe.akka" %% "akka-stream" % "2.5.6",
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.6" % Test
)
