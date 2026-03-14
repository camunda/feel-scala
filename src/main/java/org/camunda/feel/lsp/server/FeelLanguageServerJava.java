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
package org.camunda.feel.lsp.server;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.concurrent.CompletableFuture;

public final class FeelLanguageServerJava implements LanguageServer, LanguageClientAware {

  private final FeelLanguageServer delegate = new FeelLanguageServer();
  private final TextDocumentService textDocumentService =
      new TextDocumentServiceForwarder(delegate.getTextDocumentService());
  private final WorkspaceService workspaceService =
      new WorkspaceServiceForwarder(delegate.getWorkspaceService());

  @Override
  public void connect(LanguageClient client) {
    delegate.connect(client);
  }

  @Override
  public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
    return delegate.initialize(params);
  }

  @Override
  public CompletableFuture<Object> shutdown() {
    return delegate.shutdown();
  }

  @Override
  public void exit() {
    delegate.exit();
  }

  @Override
  public TextDocumentService getTextDocumentService() {
    return textDocumentService;
  }

  @Override
  public WorkspaceService getWorkspaceService() {
    return workspaceService;
  }
}

