name := "poe-tracker"

version := "0.1"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-generic"
).map(_ % "0.9.0-M1")

libraryDependencies += "de.heikoseeberger" %% "akka-http-circe" % "1.15.0"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.6",
  "com.typesafe.akka" %% "akka-stream" % "2.5.6",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.6" % Test
)

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.10"
