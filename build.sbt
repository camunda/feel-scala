import org.scalajs.linker.interface.ESVersion
import scalanativecrossproject.NativePlatform

lazy val installJS = taskKey[Unit]("Compile JS and copy to feel-playground/vendor")
Global / onChangedBuildSource := ReloadOnSourceChanges

val sharedSettings = Seq(
  scalaVersion      := "2.13.18",
  name              := "core",
  libraryDependencies ++= Seq(
    "com.lihaoyi"       %%% "fastparse"            % "3.1.1",
    "org.scala-js"       %% "scalajs-stubs"        % "1.1.0",
    "com.lihaoyi"       %%% "ujson"                % "4.1.0",
    "org.scalatest"     %%% "scalatest"            % "3.2.19" % Test,
    "io.github.cquiroz" %%% "scala-java-time"      % "2.6.0",
    "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.6.0"
  ),
  Compile / compile := (Compile / compile).dependsOn(Compile / scalafmt).value,
  Test / compile    := (Test / compile).dependsOn(Test / scalafmt).value
)
lazy val core      = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .withoutSuffixFor(JVMPlatform)
  .settings(sharedSettings)
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.apache.logging.log4j" % "log4j-api"        % "2.25.2",
      "org.apache.logging.log4j" % "log4j-core"       % "2.25.2",
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.25.2"
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0"
    ),
    scalaJSLinkerConfig ~= {
      _.withESFeatures(_.withESVersion(ESVersion.ES2018))
        .withClosureCompilerIfAvailable(true)
        .withMinify(false)
        .withModuleKind(ModuleKind.CommonJSModule)
    },
    installJS := {
      val linked    = (Compile / fastLinkJS).value
      val outputDir = (Compile / fastLinkJSOutput).value
      val targetDir = baseDirectory.value / ".." / ".." / ".." / "feel-playground" / "vendor"
      for {
        jsFileName <- linked.data.publicModules.map(_.jsFileName)
      } {
        val jsFile  = outputDir / jsFileName
        val mapFile = outputDir / (jsFileName + ".map")
        IO.copyFile(jsFile, targetDir / jsFileName)
        println(s"Copied $jsFile to ${targetDir / jsFileName}")
        if (mapFile.exists()) {
          IO.copyFile(mapFile, targetDir / (jsFileName + ".map"))
          println(s"Copied $mapFile to ${targetDir / (jsFileName + ".map")}")
        }
      }
    }
  )
  .nativeSettings(
    libraryDependencies ++= Seq(
    )
  )

lazy val cli = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .withoutSuffixFor(JVMPlatform)
  .dependsOn(core)
  .settings(
    scalaVersion := "2.13.18",
    name         := "cli",
    libraryDependencies ++= Seq(
      "com.github.alexarchambault" %%% "case-app"  % "2.1.0",
      "org.scalatest"              %%% "scalatest" % "3.2.19" % Test
    )
  )
  .jsSettings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withESFeatures(_.withESVersion(ESVersion.ES2018))
        .withClosureCompiler(false)
        .withMinify(false)
        .withModuleKind(ModuleKind.CommonJSModule)
    }
  )
  .nativeSettings(
  )
