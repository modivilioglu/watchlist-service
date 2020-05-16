//name := "MyProject"
//version := "1.0"
//scalaVersion := "2.12.0"
//lazy val akkaHttpVersion = "10.0.11"
//lazy val akkaVersion    = "2.5.11"
//
//libraryDependencies ++= Seq(
//  "org.scalactic" %% "scalactic" % "3.0.5",
//  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
//  "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
//  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
//  "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
//  "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
//
//  "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
//  "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
//  "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
//  "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test
//)

lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.11"
scalacOptions += "-Ypartial-unification"
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.mod",
      scalaVersion    := "2.12.4"
    )),
    name := "watchlist-service",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
      "org.typelevel" %% "cats-core" % "1.4.0",
      "com.google.inject" % "guice" % "2.0",
      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test,
      "org.mockito" % "mockito-all" % "1.10.19" % Test,
      "org.typelevel" %% "cats-core" % "1.4.0",
      "org.typelevel" %% "cats-effect" % "1.0.0"
    )
  )

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
