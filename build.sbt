name := "GnipListener"

version := "0.1"

scalaVersion := "2.11.8"
val akkaVersion = "2.4.11"

mainClass in Compile := Some("com.mele.twitter.GnipListener")
lazy val root = (project in file(".")).enablePlugins(JavaServerAppPackaging)

resolvers += "scalac repo" at "https://raw.githubusercontent.com/ScalaConsultants/mvn-repo/master/"

libraryDependencies ++= Seq(
  "com.typesafe.akka"       %% "akka-actor"             % {akkaVersion},
  "com.typesafe.akka"       %% "akka-slf4j"             % {akkaVersion},
  "ch.qos.logback"          % "logback-classic"         % "1.1.3",
  "com.typesafe.akka"       %% "akka-http-core"         % {akkaVersion},
  "com.typesafe.akka"       %% "akka-stream"            % {akkaVersion},
  "com.typesafe.akka"       %% "akka-http-experimental" % {akkaVersion},
  "org.json4s"              %% "json4s-jackson"         % "3.4.2"
)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-language:postfixOps", "-language:reflectiveCalls")
