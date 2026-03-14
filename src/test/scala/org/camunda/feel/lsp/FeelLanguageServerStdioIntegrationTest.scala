/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.feel.lsp

import org.eclipse.lsp4j.launch.LSPLauncher
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.{
  DidCloseTextDocumentParams,
  DidOpenTextDocumentParams,
  InitializeParams,
  TextDocumentIdentifier,
  TextDocumentItem
}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.{File, InputStream}
import java.nio.file.Paths
import java.util.concurrent.TimeoutException
import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters.CollectionHasAsScala

class FeelLanguageServerStdioIntegrationTest extends AnyFlatSpec with Matchers {

  "The FEEL language server stdio process" should "handle initialize and diagnostics lifecycle" in {
    val process = startServerProcess()

    try {
      val client                 = new RecordingLanguageClientJava()
      val launcher               =
        LSPLauncher.createClientLauncher(client, process.getInputStream, process.getOutputStream)
      val server: LanguageServer = launcher.getRemoteProxy

      launcher.startListening()

      val initializeResult =
        try {
          server.initialize(new InitializeParams()).get(5, TimeUnit.SECONDS)
        } catch {
          case timeout: TimeoutException =>
            fail(
              s"Timed out waiting for initialize response. stderr: '${readAvailable(process.getErrorStream)}'",
              timeout
            )
        }
      initializeResult.getCapabilities.getCompletionProvider should not be null

      val uri = "file:///integration.feel"
      server.getTextDocumentService.didOpen(
        new DidOpenTextDocumentParams(
          new TextDocumentItem(
            uri,
            "feel",
            1,
            "x +"
          )
        )
      )

      val openDiagnostics = client.awaitDiagnostics(3000)
      openDiagnostics should not be null
      openDiagnostics.getDiagnostics.asScala should not be empty

      server.getTextDocumentService.didClose(
        new DidCloseTextDocumentParams(new TextDocumentIdentifier(uri))
      )

      val closeDiagnostics = client.awaitDiagnostics(3000)
      closeDiagnostics should not be null
      closeDiagnostics.getDiagnostics.asScala shouldBe empty

      server.shutdown().get(5, TimeUnit.SECONDS)
      server.exit()

      process.waitFor(5, TimeUnit.SECONDS) should be(true)
      process.exitValue() should be(0)
    } finally {
      process.destroyForcibly()
    }
  }

  private def startServerProcess(): Process = {
    val javaBin       = s"${System.getProperty("java.home")}/bin/java"
    val classpath     = {
      val runtimeClasspath = System.getProperty("java.class.path")
      val classes          = Paths.get("target/classes").toAbsolutePath.toString
      val testClasses      = Paths.get("target/test-classes").toAbsolutePath.toString

      List(runtimeClasspath, classes, testClasses).mkString(File.pathSeparator)
    }
    val logConfigPath = Paths
      .get(getClass.getResource("/log4j2-lsp.xml").toURI)
      .toAbsolutePath
      .toString

    val process = new ProcessBuilder(
      javaBin,
      s"-Dlog4j2.configurationFile=$logConfigPath",
      "-cp",
      classpath,
      "org.camunda.feel.lsp.FeelLspLauncher"
    ).start()

    if (!process.isAlive) {
      fail(
        s"LSP process terminated immediately. stderr: '${readAvailable(process.getErrorStream)}'"
      )
    }

    process
  }

  private def readAvailable(stream: InputStream): String = {
    val availableBytes = stream.available()
    if (availableBytes <= 0) {
      ""
    } else {
      new String(stream.readNBytes(availableBytes))
    }
  }
}
