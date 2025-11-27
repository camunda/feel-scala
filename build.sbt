import org.scalajs.linker.interface.ESVersion
import scalanativecrossproject.NativePlatform

val sharedSettings = Seq(scalaVersion := "2.13.18",
name := "core",
libraryDependencies ++= Seq(
  "com.lihaoyi" %%% "fastparse" % "3.1.1",
  "org.scala-js" %% "scalajs-stubs" % "1.1.0",
  "com.lihaoyi" %%% "ujson" % "4.1.0"
))
lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform).crossType(CrossType.Full)
  .withoutSuffixFor(JVMPlatform)
  .settings(sharedSettings)
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % "3.2.10" % Test,
      "org.apache.logging.log4j" % "log4j-api" % "2.25.2",
      "org.apache.logging.log4j" % "log4j-core" % "2.25.2",
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.25.2",
    ))
  .jsSettings(
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % "3.2.10" % Test,
      "org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0",
      "io.github.cquiroz" %%% "scala-java-time" % "2.5.0",
      "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.5.0"
    ),
    scalaJSLinkerConfig ~= { _.withESFeatures(_.withESVersion(ESVersion.ES2018)).withClosureCompilerIfAvailable(true).withMinify(true).withModuleKind(ModuleKind.CommonJSModule)}
  )
  .nativeSettings(
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % "3.2.19" % Test,
      "io.github.cquiroz" %%% "scala-java-time" % "2.6.0",
      "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.6.0"
    )
  )
