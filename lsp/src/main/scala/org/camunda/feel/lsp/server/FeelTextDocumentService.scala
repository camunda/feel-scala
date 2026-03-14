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
import java.util.concurrent.CompletableFuture
import scala.jdk.CollectionConverters.SeqHasAsJava

class FeelTextDocumentService(
    store: DocumentStore,
    analyzer: FeelAnalyzer,
    clientProvider: () => LanguageClient
) extends TextDocumentService {

  private val logger = FeelLspLogging.logger(getClass.getName)

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
      publishInterpreterDiagnosticsAsync(state)
    }
  }

  override def didClose(params: DidCloseTextDocumentParams): Unit = {
    logger.finest(
      s"TRACE Received request: textDocument/didClose uri='${params.getTextDocument.getUri}'"
    )
    store.remove(params.getTextDocument.getUri)
    val diagnostics = new PublishDiagnosticsParams(
      params.getTextDocument.getUri,
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
      CompletableFuture
        .supplyAsync(() => analyzer.analyzeInterpreter(state.text))
        .thenAccept(warnings => {
          store.withInterpreterDiagnostics(state.uri, state.version, warnings).foreach {
            mergedState =>
              logger.finest(
                s"TRACE Publish interpreter diagnostics: uri='${mergedState.uri}' version=${mergedState.version} count=${warnings.size}"
              )
              publishDiagnostics(mergedState)
          }
        })
        .exceptionally(error => {
          logger.warning(
            s"Failed to compute interpreter diagnostics for uri='${state.uri}' version=${state.version}: ${error.getMessage}"
          )
          null
        })
      ()
    }
  }

  private def hasParserError(state: DocumentState): Boolean =
    state.analysis.diagnostics.exists(d => d.getSource == "feel-parser")

  private def versionOf(version: Integer): Int = Option(version).map(_.intValue()).getOrElse(0)
}
