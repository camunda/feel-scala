organization := "org.camunda.bpm.extension.feel.scala"
name := "feel-engine"
version := "1.3.0-SNAPSHOT"

scalaVersion := "2.11.7"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= List(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4" ,
  "com.github.nscala-time" %% "nscala-time" % "2.2.0"
)

libraryDependencies ++= List(
	"org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
	"junit" % "junit" % "4.11" % "test"
)

assemblyJarName in assembly :=  s"${name.value}-${version.value}-complete.jar"
