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

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

final class TextDocumentServiceForwarder implements TextDocumentService {

  private final TextDocumentService delegate;

  TextDocumentServiceForwarder(TextDocumentService delegate) {
    this.delegate = delegate;
  }

  @Override
  public void didOpen(DidOpenTextDocumentParams params) {
    delegate.didOpen(params);
  }

  @Override
  public void didChange(DidChangeTextDocumentParams params) {
    delegate.didChange(params);
  }

  @Override
  public void didClose(DidCloseTextDocumentParams params) {
    delegate.didClose(params);
  }

  @Override
  public void didSave(DidSaveTextDocumentParams params) {
    delegate.didSave(params);
  }

  @Override
  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
      CompletionParams position) {
    return delegate.completion(position);
  }

  @Override
  public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem unresolved) {
    return delegate.resolveCompletionItem(unresolved);
  }

  @Override
  public CompletableFuture<Hover> hover(HoverParams params) {
    return delegate.hover(params);
  }

  @Override
  public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
    return delegate.semanticTokensFull(params);
  }
}


