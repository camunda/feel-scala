val shared = Seq(
  organization := "org.camunda.feel",
  version := "1.13.0-SNAPSHOT",
  scalaVersion := "2.13.4",
  resolvers += Resolver.mavenLocal,
  resolvers += Classpaths.typesafeReleases,
  resolvers += "camunda-bpm-nexus" at "https://app.camunda.com/nexus/content/groups/public",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
)

val camundaVersion = "7.11.0"

lazy val engine = crossProject(JSPlatform, JVMPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .enablePlugins(AssemblyPlugin)
  .settings(
    shared,
    name := "feel-engine",
    description := "FEEL engine",
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % "3.1.4" % "test",
      "com.lihaoyi" %%% "fastparse" % "2.3.0"
    ),
    // Workaround for https://github.com/portable-scala/sbt-crossproject/issues/74
    Seq(Compile, Test).flatMap(inConfig(_) {
      unmanagedResourceDirectories ++= {
        unmanagedSourceDirectories.value
          .map(src => (src / ".." / "resources").getCanonicalFile)
          .filterNot(unmanagedResourceDirectories.value.contains)
          .distinct
      }
    }),
    assemblyJarName in assembly := s"${name.value}-${version.value}-complete.jar"
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "junit" % "junit" % "4.11" % "test",
      "org.apache.logging.log4j" % "log4j-api" % "2.9.0" % "test",
      "org.apache.logging.log4j" % "log4j-core" % "2.9.0" % "test",
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.9.0" % "test"
    )
  )
  .jsConfigure(_.enablePlugins(TzdbPlugin))
  .jsSettings(
    libraryDependencies ++= Seq(
      "io.github.cquiroz" %%% "scala-java-time" % "2.1.0",
      "io.github.cquiroz" %%% "scala-java-locales" % "1.1.0"
    )
  )
