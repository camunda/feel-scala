organization := "org.camunda"

name := "camunda-feel-scala-factory"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.7"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "camunda-bpm-nexus" at "https://app.camunda.com/nexus/content/groups/public"
 
libraryDependencies ++= List(
  "org.camunda" %% "feel-scala" % "1.0.0-SNAPSHOT"
  "org.camunda.bpm.dmn" % "camunda-engine-feel-api" % "7.6.0-alpha6" % "provided"
) 
 
libraryDependencies ++= List(
	"org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
	"junit" % "junit" % "4.11" % "test",
	"org.camunda.bpm.dmn" % "camunda-engine-dmn" % "7.6.0-alpha6" % "test"
)

