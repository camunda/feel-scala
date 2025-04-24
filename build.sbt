import org.scalajs.linker.interface.ESVersion

val sharedSettings = Seq(scalaVersion := "2.13.16",
name := "core",
libraryDependencies ++= Seq(
  "org.scalatest" %%% "scalatest" % "3.2.10" % Test,
  "com.lihaoyi" %%% "fastparse" % "3.1.1",
  "org.scala-js" %% "scalajs-stubs" % "1.1.0",
))
lazy val core = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Full)
  .withoutSuffixFor(JVMPlatform)
  .settings(sharedSettings)
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.apache.logging.log4j" % "log4j-api" % "2.24.3",
      "org.apache.logging.log4j" % "log4j-core" % "2.24.3",
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.24.3",
    ))
  .jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0",
      "io.github.cquiroz" %%% "scala-java-time" % "2.5.0"
    ),
    scalaJSLinkerConfig ~= { _.withESFeatures(_.withESVersion(ESVersion.ES2018)).withClosureCompilerIfAvailable(true).withMinify(true).withModuleKind(ModuleKind.CommonJSModule)}
  )
//  .nativePlatform(scalaVersions = Seq("2.13.16"))
