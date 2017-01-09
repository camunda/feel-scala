name := "camunda-feel-scala-factory"
 
libraryDependencies ++= List(
  "org.camunda.bpm.dmn" % "camunda-engine-feel-api" % "7.6.0-alpha6" % "provided"
) 
 
libraryDependencies ++= List(
	"org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
	"junit" % "junit" % "4.11" % "test",
	"org.camunda.bpm.dmn" % "camunda-engine-dmn" % "7.6.0-alpha6" % "test"
)

