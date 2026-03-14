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

import org.camunda.feel.lsp.analysis.FeelAnalyzer
import org.camunda.feel.lsp.server.FeelLanguageServer
import org.eclipse.lsp4j.{
  CompletionParams,
  DiagnosticSeverity,
  DidChangeTextDocumentParams,
  DidOpenTextDocumentParams,
  HoverParams,
  InitializeParams,
  Position,
  SemanticTokensParams,
  TextDocumentContentChangeEvent,
  TextDocumentIdentifier,
  TextDocumentItem,
  VersionedTextDocumentIdentifier
}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.concurrent.{CountDownLatch, TimeUnit}
import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}
import scala.jdk.CollectionConverters.CollectionHasAsScala

class FeelLanguageServerProtocolTest extends AnyFlatSpec with Matchers {

  "The FEEL language server" should "advertise capabilities and publish parse diagnostics" in {
    val server = new FeelLanguageServer()
    val client = new RecordingLanguageClient()
    server.connect(client)

    val initializeResult = server.initialize(new InitializeParams()).get()

    initializeResult.getCapabilities.getHoverProvider.getLeft.booleanValue() should be(true)
    initializeResult.getCapabilities.getCompletionProvider should not be null
    val semanticTokensProvider = initializeResult.getCapabilities.getSemanticTokensProvider
    semanticTokensProvider should not be null
    semanticTokensProvider.getId should be("feel-semantic-tokens")
    semanticTokensProvider.getFull should not be null
    semanticTokensProvider.getRange should not be null

    val fullEnabled =
      if (semanticTokensProvider.getFull.isLeft) {
        semanticTokensProvider.getFull.getLeft.booleanValue()
      } else {
        semanticTokensProvider.getFull.getRight != null
      }
    fullEnabled should be(true)

    val rangeEnabled =
      if (semanticTokensProvider.getRange.isLeft) {
        semanticTokensProvider.getRange.getLeft.booleanValue()
      } else {
        semanticTokensProvider.getRange.getRight == java.lang.Boolean.TRUE
      }
    rangeEnabled should be(false)

    val tokenLegend = semanticTokensProvider.getLegend
    tokenLegend.getTokenTypes.asScala should contain allOf (
      "keyword",
      "function",
      "variable",
      "string",
      "number"
    )

    val experimental =
      initializeResult.getCapabilities.getExperimental.asInstanceOf[java.util.Map[String, Object]]
    experimental.get("feelLanguageVersion") should be(FeelAnalyzer.FeelLanguageVersion)

    server.getTextDocumentService.didOpen(
      new DidOpenTextDocumentParams(
        new TextDocumentItem(
          "file:///expr.feel",
          "feel",
          1,
          "x +"
        )
      )
    )

    val diagnostics = client.awaitDiagnostics()
    diagnostics should not be null
    diagnostics.getDiagnostics.asScala should have size 1
    diagnostics.getDiagnostics.get(0).getMessage should startWith("Parse error:")
  }

  it should "publish warning diagnostics, completion and hover data" in {
    val server = new FeelLanguageServer()
    val client = new RecordingLanguageClient()
    server.connect(client)

    server.initialize(new InitializeParams()).get()

    val uri = "file:///warn.feel"
    server.getTextDocumentService.didOpen(
      new DidOpenTextDocumentParams(
        new TextDocumentItem(
          uri,
          "feel",
          1,
          "substring(\"abc\", 1)"
        )
      )
    )

    val diagnostics = client.awaitDiagnostics()
    diagnostics should not be null
    diagnostics.getDiagnostics.asScala.exists(_.getMessage.contains("NO_VARIABLE_FOUND")) should be(
      false
    )

    val completionParams = new CompletionParams(new TextDocumentIdentifier(uri), new Position(0, 0))
    val completionResult = server.getTextDocumentService.completion(completionParams).get()
    val completionItems  = completionResult match {
      case left if left.isLeft    => left.getLeft.asScala.toList
      case right if right.isRight => right.getRight.getItems.asScala.toList
    }

    completionItems.exists(_.getLabel == "substring") should be(true)
    completionItems.exists(_.getLabel == "x") should be(false)

    val hover = server.getTextDocumentService
      .hover(
        new HoverParams(new TextDocumentIdentifier(uri), new Position(0, 4))
      )
      .get()

    hover should not be null
    hover.getContents.getRight.getValue should include("substring")
  }

  it should "publish diagnostics in fast and interpreter phases" in {
    val analyzer = new FeelAnalyzer() {
      override def analyzeInterpreter(text: String): List[org.eclipse.lsp4j.Diagnostic] = {
        val diagnostic = new org.eclipse.lsp4j.Diagnostic(
          FeelAnalyzer.fullRange(text),
          "FUNCTION_INVOCATION_FAILURE: simulated warning"
        )
        diagnostic.setSeverity(DiagnosticSeverity.Warning)
        diagnostic.setSource("feel-interpreter")
        List(diagnostic)
      }
    }

    val server = new FeelLanguageServer(analyzer)
    val client = new RecordingLanguageClient()
    server.connect(client)
    server.initialize(new InitializeParams()).get()

    val uri        = "file:///phased-diagnostics.feel"
    val expression = "1 + 1"

    server.getTextDocumentService.didOpen(
      new DidOpenTextDocumentParams(
        new TextDocumentItem(uri, "feel", 1, expression)
      )
    )

    val fastDiagnostics = client.awaitDiagnostics()
    fastDiagnostics should not be null
    fastDiagnostics.getVersion should be(1)
    fastDiagnostics.getDiagnostics.asScala shouldBe empty

    val interpreterDiagnostics = client.awaitDiagnostics()
    interpreterDiagnostics should not be null
    interpreterDiagnostics.getVersion should be(1)
    interpreterDiagnostics.getDiagnostics.asScala.exists(
      _.getSource == "feel-interpreter"
    ) should be(
      true
    )
  }

  it should "publish a timeout error diagnostic for long-running interpreter evaluations" in {
    val timeoutMillis   = 50L
    val enteredEval     = new CountDownLatch(1)
    val interruptedEval = new CountDownLatch(1)
    val blocker         = new CountDownLatch(1)

    val analyzer = new FeelAnalyzer() {
      override def analyzeInterpreter(text: String): List[org.eclipse.lsp4j.Diagnostic] = {
        enteredEval.countDown()
        try {
          blocker.await()
          List.empty
        } catch {
          case interrupted: InterruptedException =>
            interruptedEval.countDown()
            throw interrupted
        }
      }
    }

    val server = new FeelLanguageServer(analyzer, timeoutMillis)
    val client = new RecordingLanguageClient()
    server.connect(client)
    server.initialize(new InitializeParams()).get()

    val uri        = "file:///timeout-diagnostics.feel"
    val expression = "1 + 1"

    server.getTextDocumentService.didOpen(
      new DidOpenTextDocumentParams(
        new TextDocumentItem(uri, "feel", 1, expression)
      )
    )

    val fastDiagnostics = client.awaitDiagnostics()
    fastDiagnostics should not be null
    fastDiagnostics.getVersion should be(1)
    fastDiagnostics.getDiagnostics.asScala shouldBe empty

    enteredEval.await(1, TimeUnit.SECONDS) should be(true)

    val startedAt          = System.nanoTime()
    val timeoutDiagnostics = client.awaitDiagnostics(1000)
    val elapsedMillis      = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt)

    timeoutDiagnostics should not be null
    timeoutDiagnostics.getVersion should be(1)
    timeoutDiagnostics.getDiagnostics.asScala should have size 1

    val timeoutDiagnostic = timeoutDiagnostics.getDiagnostics.get(0)
    timeoutDiagnostic.getSeverity should be(DiagnosticSeverity.Error)
    timeoutDiagnostic.getSource should be("feel-interpreter")
    timeoutDiagnostic.getMessage should include(s"timed out after $timeoutMillis ms")

    interruptedEval.await(1, TimeUnit.SECONDS) should be(true)
    elapsedMillis should be < 1000L
  }

  it should "ignore stale didChange versions" in {
    val server = new FeelLanguageServer()
    val client = new RecordingLanguageClient()
    server.connect(client)
    server.initialize(new InitializeParams()).get()

    val uri = "file:///version.feel"
    server.getTextDocumentService.didOpen(
      new DidOpenTextDocumentParams(
        new TextDocumentItem(
          uri,
          "feel",
          2,
          "x +"
        )
      )
    )

    client.awaitDiagnostics() should not be null

    val staleChange = new DidChangeTextDocumentParams(
      new VersionedTextDocumentIdentifier(uri, 1),
      java.util.Collections.singletonList(new TextDocumentContentChangeEvent("1 + 2"))
    )

    server.getTextDocumentService.didChange(staleChange)

    client.awaitDiagnostics(250) should be(null)
  }

  it should "suppress stale interpreter diagnostics for older versions" in {
    val firstEvalStarted = new CountDownLatch(1)
    val releaseFirstEval = new CountDownLatch(1)
    val interpreterRuns  = new AtomicInteger(0)

    val analyzer = new FeelAnalyzer() {
      override def analyzeInterpreter(text: String): List[org.eclipse.lsp4j.Diagnostic] = {
        val run = interpreterRuns.incrementAndGet()

        if (run == 1) {
          firstEvalStarted.countDown()
          releaseFirstEval.await()
          List(newInterpreterDiagnostic(text, "stale-warning"))
        } else {
          List(newInterpreterDiagnostic(text, "latest-warning"))
        }
      }
    }

    val server = new FeelLanguageServer(analyzer)
    val client = new RecordingLanguageClient()
    server.connect(client)
    server.initialize(new InitializeParams()).get()

    val uri = "file:///stale-interpreter.feel"

    server.getTextDocumentService.didOpen(
      new DidOpenTextDocumentParams(
        new TextDocumentItem(
          uri,
          "feel",
          1,
          "substring(123, 1, 2) + unknownVar"
        )
      )
    )

    val openFast = client.awaitDiagnostics()
    openFast should not be null
    openFast.getVersion should be(1)

    firstEvalStarted.await(1, TimeUnit.SECONDS) should be(true)

    server.getTextDocumentService.didChange(
      new DidChangeTextDocumentParams(
        new VersionedTextDocumentIdentifier(uri, 2),
        java.util.Collections.singletonList(
          new TextDocumentContentChangeEvent("substring(\"abc\", 1)")
        )
      )
    )

    val changeFast = client.awaitDiagnostics(500)
    changeFast should not be null
    changeFast.getVersion should be(2)
    changeFast.getDiagnostics.asScala shouldBe empty

    val changeInterpreter = client.awaitDiagnostics(1000)
    changeInterpreter should not be null
    changeInterpreter.getVersion should be(2)
    changeInterpreter.getDiagnostics.asScala.exists(
      _.getMessage.contains("latest-warning")
    ) should be(
      true
    )

    releaseFirstEval.countDown()

    // Completing the stale version-1 evaluation must not publish diagnostics anymore.
    client.awaitDiagnostics(300) should be(null)
  }

  it should "interrupt long-running interpreter diagnostics evaluation threads" in {
    val evaluatorThread = new AtomicReference[Thread]()
    val enteredEval     = new CountDownLatch(1)
    val interruptedEval = new CountDownLatch(1)

    val analyzer = new FeelAnalyzer() {
      override def analyzeInterpreter(text: String): List[org.eclipse.lsp4j.Diagnostic] = {
        evaluatorThread.set(Thread.currentThread())
        enteredEval.countDown()
        try {
          super.analyzeInterpreter(text)
        } catch {
          case interrupted: InterruptedException =>
            interruptedEval.countDown()
            throw interrupted
        }
      }
    }

    val server = new FeelLanguageServer(analyzer)
    val client = new RecordingLanguageClient()
    server.connect(client)
    server.initialize(new InitializeParams()).get()

    val uri        = "file:///interrupt-interpreter.feel"
    val expression =
      "for i in 1..10000 return for j in 1..100000 return i * j"

    server.getTextDocumentService.didOpen(
      new DidOpenTextDocumentParams(
        new TextDocumentItem(uri, "feel", 1, expression)
      )
    )

    val fastDiagnostics = client.awaitDiagnostics()
    fastDiagnostics should not be null
    fastDiagnostics.getVersion should be(1)
    fastDiagnostics.getDiagnostics.asScala shouldBe empty

    enteredEval.await(3, TimeUnit.SECONDS) should be(true)

    val thread = evaluatorThread.get()
    thread should not be null
    thread.interrupt()

    interruptedEval.await(3, TimeUnit.SECONDS) should be(true)

    // Interrupted interpreter evaluation should not publish late diagnostics for this version.
    client.awaitDiagnostics(1000) should be(null)
  }

  it should "return semantic tokens for FEEL snippets" in {
    val server = new FeelLanguageServer()
    val client = new RecordingLanguageClient()
    server.connect(client)
    server.initialize(new InitializeParams()).get()

    val uri  = "file:///tokens.feel"
    val text = "unknownVar + substring(\"abc\", 42) and true and null"

    server.getTextDocumentService.didOpen(
      new DidOpenTextDocumentParams(
        new TextDocumentItem(uri, "feel", 1, text)
      )
    )

    client.awaitDiagnostics() should not be null

    val tokens = server.getTextDocumentService
      .semanticTokensFull(new SemanticTokensParams(new TextDocumentIdentifier(uri)))
      .get()

    tokens should not be null
    val spans  = decodeSemanticTokens(text, tokens.getData.asScala.toList.map(_.intValue()))

    spans should contain(TokenSpan("unknownVar", "variable"))
    spans should contain(TokenSpan("substring", "function"))
    spans should contain(TokenSpan("\"abc\"", "string"))
    spans should contain(TokenSpan("42", "number"))
    spans should contain(TokenSpan("and", "keyword"))
    spans should contain(TokenSpan("true", "keyword"))
    spans should contain(TokenSpan("null", "keyword"))
  }

  it should "return semantic tokens for latest didChange content" in {
    val server = new FeelLanguageServer()
    val client = new RecordingLanguageClient()
    server.connect(client)
    server.initialize(new InitializeParams()).get()

    val uri = "file:///tokens-change.feel"

    server.getTextDocumentService.didOpen(
      new DidOpenTextDocumentParams(
        new TextDocumentItem(uri, "feel", 1, "x + 1")
      )
    )

    client.awaitDiagnostics() should not be null

    server.getTextDocumentService.didChange(
      new DidChangeTextDocumentParams(
        new VersionedTextDocumentIdentifier(uri, 2),
        java.util.Collections.singletonList(
          new TextDocumentContentChangeEvent("substring(\"a\", 1)")
        )
      )
    )

    client.awaitDiagnostics() should not be null

    val updatedText = "substring(\"a\", 1)"
    val tokens      = server.getTextDocumentService
      .semanticTokensFull(new SemanticTokensParams(new TextDocumentIdentifier(uri)))
      .get()

    tokens should not be null
    val spans       = decodeSemanticTokens(updatedText, tokens.getData.asScala.toList.map(_.intValue()))

    spans should contain(TokenSpan("substring", "function"))
    spans should contain(TokenSpan("\"a\"", "string"))
    spans should contain(TokenSpan("1", "number"))
    spans.exists(_.lexeme == "x") should be(false)
  }

  it should "return empty semantic tokens for unknown documents" in {
    val server = new FeelLanguageServer()
    val client = new RecordingLanguageClient()
    server.connect(client)
    server.initialize(new InitializeParams()).get()

    val tokens = server.getTextDocumentService
      .semanticTokensFull(
        new SemanticTokensParams(new TextDocumentIdentifier("file:///missing.feel"))
      )
      .get()

    tokens should not be null
    tokens.getData.asScala shouldBe empty
  }

  private def decodeSemanticTokens(text: String, data: List[Int]): List[TokenSpan] = {
    val tokenTypes = List("keyword", "function", "variable", "string", "number")
    val lines      = text.split("\\n", -1)

    var line   = 0
    var column = 0

    data
      .grouped(5)
      .map { values =>
        val deltaLine  = values(0)
        val deltaStart = values(1)
        val length     = values(2)
        val tokenType  = tokenTypes(values(3))

        line = line + deltaLine
        column = if (deltaLine == 0) column + deltaStart else deltaStart

        val lexeme = lines(line).substring(column, column + length)
        TokenSpan(lexeme, tokenType)
      }
      .toList
  }

  private def newInterpreterDiagnostic(
      text: String,
      message: String
  ): org.eclipse.lsp4j.Diagnostic = {
    val diagnostic = new org.eclipse.lsp4j.Diagnostic(FeelAnalyzer.fullRange(text), message)
    diagnostic.setSeverity(DiagnosticSeverity.Warning)
    diagnostic.setSource("feel-interpreter")
    diagnostic
  }

}

case class TokenSpan(lexeme: String, tokenType: String)
