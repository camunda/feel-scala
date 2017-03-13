organization := "org.camunda.bpm.extension.feel.scala"
name := "examples"
version := "1.1.0-SNAPSHOT"
 
scalaVersion := "2.11.7"

resolvers ++= Seq(
	"Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
	"camunda-bpm-nexus" at "https://app.camunda.com/nexus/content/groups/public"
) 

libraryDependencies += "org.camunda.bpm.extension.feel.scala" %% "feel-engine-factory" % "1.1.0-SNAPSHOT"
 
libraryDependencies ++= List(
	"org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
	"junit" % "junit" % "4.11" % "test",
	"org.camunda.bpm.dmn" % "camunda-engine-dmn" % "7.7.0-alpha1" % "test"
)

