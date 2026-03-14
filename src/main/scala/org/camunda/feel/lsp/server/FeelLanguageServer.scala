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
import org.camunda.feel.lsp.model.DocumentStore
import org.eclipse.lsp4j.services.{
  LanguageClient,
  LanguageClientAware,
  LanguageServer,
  TextDocumentService,
  WorkspaceService
}
import org.eclipse.lsp4j.{
  CompletionOptions,
  InitializeParams,
  InitializeResult,
  ServerCapabilities,
  ServerInfo,
  TextDocumentSyncKind
}

import java.util
import java.util.concurrent.CompletableFuture

class FeelLanguageServer extends LanguageServer with LanguageClientAware {

  private val analyzer = new FeelAnalyzer()
  private val store    = new DocumentStore()

  @volatile private var client: LanguageClient         = _
  @volatile private var shutdownRequested: Boolean     = false
  private val textDocumentService: TextDocumentService =
    new FeelTextDocumentService(store, analyzer, () => client)
  private val workspaceService: WorkspaceService       = new FeelWorkspaceService()

  override def connect(client: LanguageClient): Unit = {
    this.client = client
  }

  override def initialize(params: InitializeParams): CompletableFuture[InitializeResult] = {
    val serverCapabilities = new ServerCapabilities
    serverCapabilities.setTextDocumentSync(TextDocumentSyncKind.Full)
    serverCapabilities.setCompletionProvider(new CompletionOptions())
    serverCapabilities.setHoverProvider(true)

    val result       = new InitializeResult(serverCapabilities)
    val experimental = new util.HashMap[String, Object]()
    experimental.put("feelLanguageVersion", analyzer.feelLanguageVersion)
    serverCapabilities.setExperimental(experimental)

    result.setServerInfo(new ServerInfo("feel-lsp-server", FeelLanguageServer.serverVersion))

    CompletableFuture.completedFuture(result)
  }

  override def shutdown(): CompletableFuture[AnyRef] = {
    shutdownRequested = true
    CompletableFuture.completedFuture(null)
  }

  override def exit(): Unit = {
    val exitCode = if (shutdownRequested) 0 else 1
    System.exit(exitCode)
  }

  override def getTextDocumentService: TextDocumentService = textDocumentService

  override def getWorkspaceService: WorkspaceService = workspaceService
}

object FeelLanguageServer {

  private val serverVersionFallback: String = "dev"

  val serverVersion: String =
    Option(getClass.getPackage)
      .flatMap(pkg => Option(pkg.getImplementationVersion))
      .getOrElse(serverVersionFallback)
}
