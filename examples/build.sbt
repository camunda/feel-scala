organization := "org.camunda"
version := "1.0.0-SNAPSHOT"

name := "feel-examples"
 
scalaVersion := "2.11.7"

resolvers ++= Seq(
	"Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
	"camunda-bpm-nexus" at "https://app.camunda.com/nexus/content/groups/public"
) 

libraryDependencies += "org.camunda" %% "camunda-feel-integration" % "1.0.0-SNAPSHOT"
 
libraryDependencies ++= List(
	"org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
	"junit" % "junit" % "4.11" % "test",
	"org.camunda.bpm.dmn" % "camunda-engine-dmn" % "7.6.0-alpha6" % "test"
)

