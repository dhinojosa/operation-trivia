import sbt.Keys.version

lazy val triviaWeb = (project in file("trivia-web"))
  .enablePlugins(PlayScala)
  .dependsOn(triviaEntities).settings(
  name := "operation-trivia-web",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-feature", "-deprecation"),
  javacOptions ++= Seq("-Xlint:unchecked"),
  libraryDependencies ++= Seq(
    jdbc,
    cache,
    ws,
    "com.typesafe.akka" %% "akka-remote" % "2.4.14",
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
    "org.webjars" % "webjars-play_2.11" % "2.5.0-4",
    "org.webjars" % "bootstrap" % "3.3.7-1",
    "org.webjars" % "rxjs" % "2.5.3",
    "org.webjars" % "RxJS-DOM" % "4.0.1"
  )
)

lazy val aggregatorActor = (project in file("aggregator-actor"))
  .dependsOn(triviaEntities).settings(
  name := "operation-trivia-actors",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-feature", "-deprecation"),
  javacOptions ++= Seq("-Xlint:unchecked"),
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.4.13",
    "com.typesafe.akka" %% "akka-testkit" % "2.4.13",
    "com.typesafe.akka" %% "akka-remote" % "2.4.14",
    "org.scalatest" %% "scalatest" % "3.0.1",
    "org.mongodb.scala" %% "mongo-scala-driver" % "1.2.1"
  )
)

lazy val scorekeeperActor = (project in file("scorekeeper-actor"))
  .dependsOn(triviaEntities).settings(
  name := "operation-trivia-scorekeeper-actor",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-feature", "-deprecation"),
  javacOptions ++= Seq("-Xlint:unchecked"),
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.4.13",
    "com.typesafe.akka" %% "akka-testkit" % "2.4.13",
    "com.typesafe.akka" %% "akka-remote" % "2.4.14",
    "org.scalatest" %% "scalatest" % "3.0.1"
  )
)

lazy val signonActor = (project in file("signon-actor"))
  .dependsOn(triviaEntities).settings(
  name := "operation-trivia-signon-actor",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-feature", "-deprecation"),
  javacOptions ++= Seq("-Xlint:unchecked"),
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.4.13",
    "com.typesafe.akka" %% "akka-testkit" % "2.4.13",
    "com.typesafe.akka" %% "akka-remote" % "2.4.14",
    "org.scalatest" %% "scalatest" % "3.0.1"
  )
)

lazy val questionActor = (project in file("question-actor"))
  .dependsOn(triviaEntities).settings(
  name := "operation-trivia-scorekeeper-actor",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-feature", "-deprecation"),
  javacOptions ++= Seq("-Xlint:unchecked"),
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.4.13",
    "com.typesafe.akka" %% "akka-testkit" % "2.4.13",
    "com.typesafe.akka" %% "akka-remote" % "2.4.14",
    "org.scalatest" %% "scalatest" % "3.0.1",
    //"org.mongodb.scala" % "mongo-scala-driver_2.11" % "2.1.0"
    "org.mongodb.scala" %% "mongo-scala-driver" % "1.2.1"
  )
)

lazy val hostActor = (project in file("host-actor"))
  .dependsOn(triviaEntities).settings(
  name := "operation-trivia-host-actor",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-feature", "-deprecation"),
  javacOptions ++= Seq("-Xlint:unchecked"),
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.4.13",
    "com.typesafe.akka" %% "akka-testkit" % "2.4.13",
    "com.typesafe.akka" %% "akka-remote" % "2.4.14",
    "org.scalatest" %% "scalatest" % "3.0.1",
    "org.mongodb.scala" %% "mongo-scala-driver" % "1.2.1"
  )
)

lazy val triviaEntities = (project in file("trivia-entities")).settings(
  name := "operation-trivia-entities",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-feature", "-deprecation"),
  javacOptions ++= Seq("-Xlint:unchecked"),
  libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.13"
)

lazy val root =
  project.in(file("."))
    .aggregate(triviaWeb, aggregatorActor, triviaEntities, hostActor, scorekeeperActor, questionActor, signonActor)

