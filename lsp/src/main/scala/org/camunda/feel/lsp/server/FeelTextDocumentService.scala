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
package org.camunda.feel.lsp.server

import org.camunda.feel.lsp.analysis.FeelAnalyzer
import org.camunda.feel.lsp.model.{DocumentState, DocumentStore}
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.{LanguageClient, TextDocumentService}
import org.eclipse.lsp4j.{
  CompletionItem,
  CompletionList,
  CompletionParams,
  DidChangeTextDocumentParams,
  DidCloseTextDocumentParams,
  DidOpenTextDocumentParams,
  DidSaveTextDocumentParams,
  Hover,
  HoverParams,
  SemanticTokens,
  SemanticTokensParams,
  PublishDiagnosticsParams
}

import java.util
import java.util.concurrent.{
  Callable,
  CompletableFuture,
  ConcurrentHashMap,
  ExecutionException,
  ExecutorService,
  Executors,
  Future,
  ThreadFactory,
  TimeUnit,
  TimeoutException
}
import java.util.concurrent.atomic.AtomicLong
import scala.jdk.CollectionConverters.SeqHasAsJava

class FeelTextDocumentService(
    store: DocumentStore,
    analyzer: FeelAnalyzer,
    clientProvider: () => LanguageClient,
    interpreterTimeoutMillis: Long = FeelTextDocumentService.DefaultInterpreterTimeoutMillis,
    executorMode: FeelTextDocumentService.ExecutorMode = FeelTextDocumentService.DefaultExecutorMode
) extends TextDocumentService {

  private val interpreterSubmittedCounter   = new AtomicLong(0)
  private val interpreterStartedCounter     = new AtomicLong(0)
  private val interpreterTimedOutCounter    = new AtomicLong(0)
  private val interpreterInterruptedCounter = new AtomicLong(0)
  private val interpreterPublishedCounter   = new AtomicLong(0)

  private val diagnosticsExecutor: ExecutorService = executorMode match {
    case FeelTextDocumentService.ExecutorMode.Platform =>
      Executors.newCachedThreadPool(new ThreadFactory {
        override def newThread(runnable: Runnable): Thread = {
          val thread = new Thread(runnable)
          thread.setName("feel-lsp-diagnostics-orchestrator")
          thread.setDaemon(true)
          thread
        }
      })

    case FeelTextDocumentService.ExecutorMode.Virtual =>
      Executors.newVirtualThreadPerTaskExecutor()
  }

  private val interpreterExecutor: ExecutorService = executorMode match {
    case FeelTextDocumentService.ExecutorMode.Platform =>
      Executors.newCachedThreadPool(new ThreadFactory {
        override def newThread(runnable: Runnable): Thread = {
          val thread = new Thread(runnable)
          thread.setName("feel-lsp-interpreter-diagnostics")
          thread.setDaemon(true)
          thread
        }
      })

    case FeelTextDocumentService.ExecutorMode.Virtual =>
      Executors.newVirtualThreadPerTaskExecutor()
  }

  private val logger        = FeelLspLogging.logger(getClass.getName)
  private val inFlightByUri = new ConcurrentHashMap[String, InFlightInterpreterDiagnostics]()

  override def didOpen(params: DidOpenTextDocumentParams): Unit = {
    val document = params.getTextDocument
    logger.finest(
      s"TRACE Received request: textDocument/didOpen uri='${document.getUri}' version=${versionOf(document.getVersion)}"
    )
    val state    = store.put(
      uri = document.getUri,
      version = versionOf(document.getVersion),
      text = document.getText,
      analyzer = analyzer
    )

    publishDiagnostics(state)
    cancelInFlight(state.uri, "superseded by didOpen")
    publishInterpreterDiagnosticsAsync(state)
  }

  override def didChange(params: DidChangeTextDocumentParams): Unit = {
    val document  = params.getTextDocument
    logger.finest(
      s"TRACE Received request: textDocument/didChange uri='${document.getUri}' version=${versionOf(document.getVersion)}"
    )
    val maybeOpen = store.update(
      uri = document.getUri,
      version = versionOf(document.getVersion),
      changes = params.getContentChanges,
      analyzer = analyzer
    )

    maybeOpen.foreach { state =>
      publishDiagnostics(state)
      cancelInFlight(state.uri, "superseded by didChange")
      publishInterpreterDiagnosticsAsync(state)
    }
  }

  override def didClose(params: DidCloseTextDocumentParams): Unit = {
    logger.finest(
      s"TRACE Received request: textDocument/didClose uri='${params.getTextDocument.getUri}'"
    )
    val uri         = params.getTextDocument.getUri
    cancelInFlight(uri, "document closed")
    store.remove(uri)
    val diagnostics = new PublishDiagnosticsParams(
      uri,
      util.Collections.emptyList[org.eclipse.lsp4j.Diagnostic]()
    )
    Option(clientProvider()).foreach(_.publishDiagnostics(diagnostics))
  }

  override def didSave(params: DidSaveTextDocumentParams): Unit = {
    logger.finest(
      s"TRACE Received request: textDocument/didSave uri='${params.getTextDocument.getUri}'"
    )
  }

  override def completion(
      params: CompletionParams
  ): CompletableFuture[Either[util.List[CompletionItem], CompletionList]] = {
    logger.finest(
      s"TRACE Received request: textDocument/completion uri='${params.getTextDocument.getUri}'"
    )
    val items = store
      .get(params.getTextDocument.getUri)
      .map(document => analyzer.completionItems(document.text, document.analysis))
      .getOrElse(util.Collections.emptyList[CompletionItem]())

    CompletableFuture.completedFuture(Either.forLeft(items))
  }

  override def hover(params: HoverParams): CompletableFuture[Hover] = {
    logger.finest(
      s"TRACE Received request: textDocument/hover uri='${params.getTextDocument.getUri}'"
    )
    val hover = store
      .get(params.getTextDocument.getUri)
      .map(document => analyzer.hover(document.text, params))
      .orNull

    CompletableFuture.completedFuture(hover)
  }

  override def semanticTokensFull(
      params: SemanticTokensParams
  ): CompletableFuture[SemanticTokens] = {
    logger.finest(
      s"TRACE Received request: textDocument/semanticTokens/full uri='${params.getTextDocument.getUri}'"
    )
    val data = store
      .get(params.getTextDocument.getUri)
      .map(document => analyzer.semanticTokenData(document.text, document.analysis))
      .getOrElse(util.Collections.emptyList[Integer]())

    CompletableFuture.completedFuture(new SemanticTokens(data))
  }

  private def publishDiagnostics(state: DocumentState): Unit = {
    logger.finest(
      s"TRACE Publish diagnostics: uri='${state.uri}' count=${state.analysis.diagnostics.size}"
    )
    val diagnostics =
      new PublishDiagnosticsParams(state.uri, state.analysis.diagnostics.asJava)
    diagnostics.setVersion(state.version)

    Option(clientProvider()).foreach(_.publishDiagnostics(diagnostics))
  }

  private def publishInterpreterDiagnosticsAsync(state: DocumentState): Unit = {
    if (hasParserError(state)) {
      ()
    } else {
      val submissionId  = interpreterSubmittedCounter.incrementAndGet()
      val evaluationRef = new java.util.concurrent.atomic.AtomicReference[Future[_]]()
      logger.finest(
        s"TRACE Interpreter diagnostics submitted: id=$submissionId uri='${state.uri}' version=${state.version}"
      )

      val orchestration = CompletableFuture
        .runAsync(
          () => {
            val started = System.nanoTime()
            interpreterStartedCounter.incrementAndGet()
            logger.finest(
              s"TRACE Interpreter diagnostics started: id=$submissionId uri='${state.uri}' version=${state.version}"
            )

            val evaluation =
              interpreterExecutor.submit(new Callable[List[org.eclipse.lsp4j.Diagnostic]] {
                override def call(): List[org.eclipse.lsp4j.Diagnostic] =
                  analyzer.analyzeInterpreter(state.text)
              })
            evaluationRef.set(evaluation)

            try {
              val warnings = evaluation.get(interpreterTimeoutMillis, TimeUnit.MILLISECONDS)
              store.withInterpreterDiagnostics(state.uri, state.version, warnings).foreach { mergedState =>
                interpreterPublishedCounter.incrementAndGet()
                logger.finest(
                  s"TRACE Publish interpreter diagnostics: id=$submissionId uri='${mergedState.uri}' version=${mergedState.version} count=${warnings.size} elapsedMs=${TimeUnit.NANOSECONDS
                    .toMillis(System.nanoTime() - started)} published=${interpreterPublishedCounter.get()}"
                )
                publishDiagnostics(mergedState)
              }
            } catch {
              case _: TimeoutException =>
                interpreterTimedOutCounter.incrementAndGet()
                evaluation.cancel(true)
                val timeoutDiagnostic = new org.eclipse.lsp4j.Diagnostic(
                  FeelAnalyzer.fullRange(state.text),
                  s"Interpreter diagnostics timed out after $interpreterTimeoutMillis ms"
                )
                timeoutDiagnostic.setSeverity(org.eclipse.lsp4j.DiagnosticSeverity.Error)
                timeoutDiagnostic.setSource("feel-interpreter")

                store
                  .withInterpreterDiagnostics(state.uri, state.version, List(timeoutDiagnostic))
                  .foreach { mergedState =>
                    logger.warning(
                      s"Interpreter diagnostics timed out: id=$submissionId uri='${mergedState.uri}' version=${mergedState.version} timeoutMs=$interpreterTimeoutMillis elapsedMs=${TimeUnit.NANOSECONDS
                        .toMillis(System.nanoTime() - started)} timedOut=${interpreterTimedOutCounter.get()}"
                    )
                    publishDiagnostics(mergedState)
                  }

              case interrupted: InterruptedException =>
                interpreterInterruptedCounter.incrementAndGet()
                Thread.currentThread().interrupt()
                logger.warning(
                  s"Interpreter diagnostics interrupted for id=$submissionId uri='${state.uri}' version=${state.version}: ${interrupted.getMessage} interrupted=${interpreterInterruptedCounter.get()}"
                )

              case execution: ExecutionException
                  if Option(execution.getCause).exists(_.isInstanceOf[InterruptedException]) =>
                interpreterInterruptedCounter.incrementAndGet()
                logger.warning(
                  s"Failed to compute interpreter diagnostics for id=$submissionId uri='${state.uri}' version=${state.version}: java.lang.InterruptedException interrupted=${interpreterInterruptedCounter.get()}"
                )

              case execution: ExecutionException =>
                logger.warning(
                  s"Failed to compute interpreter diagnostics for uri='${state.uri}' version=${state.version}: ${execution.getCause.getMessage}"
                )
            }

            val current = inFlightByUri.get(state.uri)
            if (current != null && current.submissionId == submissionId) {
              inFlightByUri.remove(state.uri, current)
            }
          },
          diagnosticsExecutor
        )
        .exceptionally(error => {
          logger.warning(
            s"Failed to compute interpreter diagnostics for uri='${state.uri}' version=${state.version}: ${error.getMessage}"
          )
          null
        })

      val registeredTask = InFlightInterpreterDiagnostics(
        submissionId = submissionId,
        evaluationRef = evaluationRef,
        orchestrator = orchestration
      )
      val previous       = inFlightByUri.put(state.uri, registeredTask)
      cancelTask(previous, state.uri, "superseded by newer interpreter diagnostics")

      ()
    }
  }

  private def cancelInFlight(uri: String, reason: String): Unit = {
    val existing = inFlightByUri.remove(uri)
    cancelTask(existing, uri, reason)
  }

  private def cancelTask(
      task: InFlightInterpreterDiagnostics,
      uri: String,
      reason: String
  ): Unit = {
    Option(task).foreach { running =>
      Option(running.orchestrator).foreach(_.cancel(true))
      Option(running.evaluationRef.get()).foreach(_.cancel(true))
      logger.finest(
        s"TRACE Cancel interpreter diagnostics: id=${running.submissionId} uri='$uri' reason='$reason'"
      )
    }
  }

  private def hasParserError(state: DocumentState): Boolean =
    state.analysis.diagnostics.exists(d => d.getSource == "feel-parser")

  private def versionOf(version: Integer): Int = Option(version).map(_.intValue()).getOrElse(0)
}

object FeelTextDocumentService {

  sealed trait ExecutorMode

  object ExecutorMode {
    case object Platform extends ExecutorMode
    case object Virtual  extends ExecutorMode
  }

  val DefaultInterpreterTimeoutMillis: Long = 5000L
  val DefaultExecutorMode: ExecutorMode     = ExecutorMode.Virtual
}

private case class InFlightInterpreterDiagnostics(
    submissionId: Long,
    evaluationRef: java.util.concurrent.atomic.AtomicReference[Future[_]],
    orchestrator: CompletableFuture[Void]
)
