
lazy val commonSettings = Seq(
  organization := "org.camunda.bpm.extension.feel.scala",
  version := "1.4.0-SNAPSHOT",
  scalaVersion := "2.12.4",

  resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  resolvers += "camunda-bpm-nexus" at "https://app.camunda.com/nexus/content/groups/public",

  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
)

val commonDependencies = Seq(
  "org.slf4j" % "slf4j-api" % "1.7.25",

  "junit" % "junit" % "4.11" % "test",
  "org.scalatest" % "scalatest_2.12" % "3.0.4" % "test",
  "org.apache.logging.log4j" % "log4j-api" % "2.9.0" % "test",
  "org.apache.logging.log4j" % "log4j-core" % "2.9.0" % "test",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.9.0" % "test"
)

val camundaVersion = "7.7.0"

lazy val root = (project in file(".")).
  settings(commonSettings).
  aggregate(engine, factory, plugin, examples)

lazy val engine = (project in file("feel-engine")).
  settings(commonSettings).
  settings(
    libraryDependencies ++= commonDependencies,
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" % "scala-parser-combinators_2.12" % "1.0.6"
    )
  )

lazy val factory = (project in file("feel-engine-factory")).
  settings(commonSettings).
  settings(
    libraryDependencies ++= commonDependencies,
    libraryDependencies ++= Seq(
      "com.github.nscala-time" % "nscala-time_2.12" % "2.16.0",
      "org.camunda.bpm.dmn" % "camunda-engine-feel-api" % camundaVersion % "provided",
      "org.camunda.bpm.dmn" % "camunda-engine-dmn" % camundaVersion % "provided"
    )
  ).
  dependsOn(engine % "test->test;compile->compile")

lazy val plugin = (project in file("feel-engine-plugin")).
  settings(commonSettings).
  settings(
    libraryDependencies ++= commonDependencies,
    libraryDependencies ++= Seq(
      "org.camunda.bpm" % "camunda-engine" % camundaVersion % "provided",

      "com.h2database" % "h2" % "1.4.193" % "test"
    )
  ).
  dependsOn(
    factory % "test->test;compile->compile",
    engine % "test->test;compile->compile"
  )

lazy val examples = (project in file("examples")).
  settings(commonSettings).
  settings(
    libraryDependencies ++= commonDependencies,
    libraryDependencies ++= Seq(
      "org.camunda.bpm.dmn" % "camunda-engine-dmn" % camundaVersion % "test"
    )
  ).
  dependsOn(
    factory % "test->test;compile->compile",
    engine % "test->test;compile->compile"
  )
