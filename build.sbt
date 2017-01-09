val appOrganization = "org.camunda"
val appVersion = "1.0.0-SNAPSHOT"

organization := appOrganization
version := appVersion
name := "feel-scala-root"

lazy val commonSettings = Seq(
  organization := appOrganization,
  version := appVersion,
  scalaVersion := "2.11.7",
  resolvers ++= Seq(
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    "camunda-bpm-nexus" at "https://app.camunda.com/nexus/content/groups/public"
  )
)

lazy val projectDependencies = List (
  appOrganization %% "feel-scala" % appVersion
)

lazy val root = project.in( file(".") )
                       .aggregate(engine, engineFactory, examples)


lazy val engine = project.in( file("feel-engine") )
                          .settings ( commonSettings )

lazy val engineFactory = project.in( file("camunda-feel-engine-factory") )
                                .settings ( commonSettings )
                                .settings ( libraryDependencies ++= projectDependencies )
                                .dependsOn(engine)
                                
lazy val examples = project.in( file("examples") )
                                .settings ( commonSettings )
                                .settings ( libraryDependencies += appOrganization %% "camunda-feel-integration" % appVersion )
                                .dependsOn(engineFactory)
